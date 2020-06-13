package at.favre.lib.dali.builder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.collection.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import at.favre.lib.dali.BuildConfig;
import at.favre.lib.dali.util.BenchmarkUtil;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;
import at.favre.lib.dali.util.Precondition;

/**
 * A simple tow level cache with a
 * a fast memory and a slow disk layer.
 */
public class TwoLevelCache {
    private static final String TAG = TwoLevelCache.class.getSimpleName();

    private static final int DISK_CACHE_SIZE_BYTE = 1024 * 1024 * 10;
    private static final int MEMORY_CACHE_SIZE_FACTOR = 10;

    private static final String DISK_CACHE_FOLDER_NAME = "dali_diskcache";
    private static final int IO_BUFFER_SIZE_BYTE = 1024 * 8;

    private static final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;

    private DiskLruCache diskLruCache;
    private BitmapLruMemoryCache memoryCache;
    private Context ctx;

    private boolean useMemoryCache;
    private boolean useDiskCache;
    private boolean debugMode;

    private int diskCacheSizeByte;
    private String diskCacheFolderName;
    private int memoryCacheSizeByte;

    public TwoLevelCache(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        this.useMemoryCache = true;
        this.useDiskCache = true;
        this.debugMode = false;
        this.diskCacheSizeByte = DISK_CACHE_SIZE_BYTE;
        this.diskCacheFolderName = DISK_CACHE_FOLDER_NAME;
        this.memoryCacheSizeByte = (int) Runtime.getRuntime().maxMemory() / MEMORY_CACHE_SIZE_FACTOR;
    }

    public TwoLevelCache(Context ctx, boolean useMemoryCache, boolean useDiskCache, int diskCacheSizeByte, String diskCacheFolderName, int memoryCacheSizeByte, boolean debugMode) {
        this.ctx = ctx;
        this.useMemoryCache = useMemoryCache;
        this.useDiskCache = useDiskCache;
        this.debugMode = debugMode;
        this.diskCacheSizeByte = diskCacheSizeByte;
        this.diskCacheFolderName = diskCacheFolderName;
        this.memoryCacheSizeByte = memoryCacheSizeByte;
    }

    public DiskLruCache getDiskCache() {
        if (diskLruCache == null) {
            try {
                diskLruCache = DiskLruCache.open(new File(LegacySDKUtil.getCacheDir(ctx), diskCacheFolderName), BuildConfig.VERSION_CODE, 1, diskCacheSizeByte);
            } catch (Exception e) {
                Log.e(TAG, "Could not create disk cache", e);
            }
        }
        return diskLruCache;
    }

    public BitmapLruMemoryCache getMemoryCache() {
        if (memoryCache == null) {
            memoryCache = new BitmapLruMemoryCache(memoryCacheSizeByte, debugMode);
        }
        return memoryCache;
    }

    public Bitmap get(String cacheKey) {
        Bitmap cache = null;
        if (useMemoryCache) {
            if ((cache = getFromMemoryCache(cacheKey)) != null) {
                BuilderUtil.logVerbose(TAG, "found in memory cache (key: " + cacheKey + ")", debugMode);
                return cache;
            }
        }

        if (useDiskCache) {
            if ((cache = getFromDiskCache(cacheKey)) != null) {
                if (useMemoryCache) {
                    putBitmapToMemoryCache(cache, cacheKey);
                }
                BuilderUtil.logVerbose(TAG, "found in disk cache (key: " + cacheKey + ")", debugMode);
            }
        }
        return cache;
    }

    public boolean putInCache(Bitmap bitmap, String cacheKey) {
        boolean memoryResult = false, diskresult = false;

        if (useMemoryCache) {
            memoryResult = putBitmapToMemoryCache(bitmap, cacheKey);
        }

        if (useDiskCache) {
            diskresult = putBitmapToDiskCache(bitmap, cacheKey);
        }

        BuilderUtil.logVerbose(TAG, "could put in memoryCache: " + memoryResult + ", could put in disk cache: " + diskresult + " (key: " + cacheKey + ")", debugMode);
        return (memoryResult || !useMemoryCache) && (diskresult || !useDiskCache);
    }

    public Bitmap getFromMemoryCache(String cacheKey) {
        Precondition.checkArgument("memory cache disabled", useMemoryCache);

        if (getMemoryCache() != null) {
            return getMemoryCache().get(cacheKey);
        }
        return null;
    }

    public boolean putBitmapToMemoryCache(Bitmap bitmap, String cacheKey) {
        Precondition.checkArgument("memory cache disabled", useMemoryCache);

        if (getMemoryCache() != null) {
            try {
                getMemoryCache().put(cacheKey, bitmap);
            } catch (Throwable t) {
                Log.e(TAG, "Could not put to memory cache", t);
            }
        }
        return false;
    }

    public Bitmap getFromDiskCache(String cacheKey) {
        Precondition.checkArgument("disk cache disabled", useDiskCache);

        if (getDiskCache() != null) {
            try {
                DiskLruCache.Snapshot snapshot = getDiskCache().get(cacheKey);
                if (snapshot != null) {
                    return BitmapFactory.decodeStream(snapshot.getInputStream(0));
                }
            } catch (IOException e) {
                Log.w(TAG, "Could not read from disk cache", e);
            }
        }
        return null;
    }

    public boolean putBitmapToDiskCache(Bitmap bitmap, String cacheKey) {
        Precondition.checkArgument("disk cache disabled", useDiskCache);

        if (getDiskCache() != null) {
            OutputStream out = null;
            try {
                DiskLruCache.Editor editor = getDiskCache().edit(cacheKey);

                if (editor != null) {
                    out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE_BYTE);
                    if (bitmap.compress(FORMAT, 100, out)) {
                        editor.commit();
                        return true;
                    } else {
                        Log.w(TAG, "Could not compress png for disk cache");
                        editor.abort();
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not write outputstream for disk cache", e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Could not close outputstream while writing cache", e);
                    }
                }
            }
        }
        return false;
    }

    public synchronized void clear() {
        clearMemoryCache();
        clearDiskCache();
    }

    public synchronized void clearMemoryCache() {
        if (memoryCache != null) {
            memoryCache.evictAll();
            memoryCache = null;
        }
    }

    public synchronized void clearDiskCache() {
        if (diskLruCache != null) {
            try {
                diskLruCache.delete();
            } catch (IOException e) {
                Log.w(TAG, "Could not clear diskcache", e);
            }
            diskLruCache = null;
        }
    }

    /**
     * Removes the value connected to the given key
     * from all levels of the cache. Will not throw an
     * exception on fail.
     *
     * @param cacheKey
     */
    public void purge(String cacheKey) {
        try {
            if (useMemoryCache) {
                if (memoryCache != null) {
                    memoryCache.remove(cacheKey);
                }
            }

            if (useDiskCache) {
                if (diskLruCache != null) {
                    diskLruCache.remove(cacheKey);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not remove entry in cache purge", e);
        }
    }

    private static class BitmapLruMemoryCache extends LruCache<String, Bitmap> {

        /**
         * @param maxSizeInBytes for caches that do not override {@link #sizeOf}, this is
         *                       the maximum number of entries in the cache. For all other caches,
         *                       this is the maximum sum of the sizes of the entries in this cache.
         */
        public BitmapLruMemoryCache(int maxSizeInBytes, boolean debugMode) {
            super(maxSizeInBytes);
            BuilderUtil.logDebug(TAG, "Create memory cache with " + BenchmarkUtil.getScalingUnitByteSize(maxSizeInBytes), debugMode);
        }

        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in bytes rather than number of items.
            return LegacySDKUtil.byteSizeOf(bitmap);
        }
    }

}
