package at.favre.lib.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * Responsibly for managing the Disk LRU Cache
 */
public class DiskCacheManager {
	private static final String TAG = Dali.class.getSimpleName();

	private static final int DISK_CACHE_SIZE_BYTE = 1024*1024*10;
	private static final String DISK_CACHE_FOLDER_NAME = "dali_diskcache";
	private static final int IO_BUFFER_SIZE_BYTE = 1024 * 8;

	private static final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;

	private DiskLruCache diskLruCache;
	private Context ctx;

	public DiskCacheManager(Context ctx) {
		this.ctx = ctx.getApplicationContext();
	}

	public DiskLruCache getDiskCache() {
		if (diskLruCache == null) {
			try {
				diskLruCache = DiskLruCache.open(new File(LegacySDKUtil.getCacheDir(ctx), DISK_CACHE_FOLDER_NAME), BuildConfig.VERSION_CODE, 1, DISK_CACHE_SIZE_BYTE);
			}catch (Exception e) {
				Log.e(TAG, "Could not create disk cache", e);
			}
		}
		return diskLruCache;
	}

	public Bitmap get(String cacheKey) {
		if(getDiskCache() != null) {
			try {
				DiskLruCache.Snapshot snapshot = getDiskCache().get(cacheKey);
				if (snapshot != null) {
					return BitmapFactory.decodeStream(snapshot.getInputStream(0));
				}
			} catch (IOException e) {
				Log.w(TAG, "Could not read from cache", e);
			}
		}
		return null;
	}

	public boolean putBitmap(Bitmap bitmap,String cacheKey) {
		if(getDiskCache() != null) {
			OutputStream out = null;
			try {
				DiskLruCache.Editor editor =getDiskCache().edit(cacheKey);

				if(editor != null) {
					out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE_BYTE);
					if(bitmap.compress(FORMAT, 100, out)) {
						editor.commit();
						return true;
					} else {
						Log.w(TAG,"Could not compress png");
						editor.abort();
					}
				}
			} catch (Exception e) {
				Log.w(TAG,"Could not write outputstream",e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						Log.w(TAG,"Could not close outputstream while writing cache",e);
					}
				}
			}
		}
		return false;
	}

}
