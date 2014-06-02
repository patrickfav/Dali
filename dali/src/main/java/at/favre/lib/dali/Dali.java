package at.favre.lib.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.favre.lib.dali.builder.ContextWrapper;
import at.favre.lib.dali.builder.ExecutorManager;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.TwoLevelCache;
import at.favre.lib.dali.builder.blur.BlurBuilder;
import at.favre.lib.dali.builder.live.LiveBlurBuilder;
import at.favre.lib.dali.util.Precondition;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class Dali {
	private static final String TAG = Dali.class.getSimpleName();

	public static final int NO_RESID = -1;

	private static TwoLevelCache DISK_CACHE_MANAGER;
	private static ExecutorManager EXECUTOR_MANAGER;
	private static Config GLOBAL_CONFIG = new Config();


	public final static class Config {
		public boolean debugMode = false;
		public boolean globalUseMemoryCache = true;
		public boolean globalUseDiskCache = true;
		public int diskCacheSizeBytes = 1024*1024*10;
		public int memoryCacheSizeBytes = (int) Runtime.getRuntime().maxMemory() / 10;
		public String diskCacheFolderName = "dali_diskcache";
		public int maxBlurWorkerThreads = 3;
		public String logTag=Dali.class.getSimpleName();
	}

	public static Config getConfig() {
		return GLOBAL_CONFIG;
	}

	/**
	 * Sets a new config and clears the previous cache
	 */
	public synchronized static void resetAndSetNewConfig(Context ctx, Config config) {
		GLOBAL_CONFIG = config;

		if(DISK_CACHE_MANAGER != null) {
			DISK_CACHE_MANAGER.clear();
			DISK_CACHE_MANAGER=null;
			createCache(ctx);
		}

		if(EXECUTOR_MANAGER != null) {
			EXECUTOR_MANAGER.shutDown();
			EXECUTOR_MANAGER = null;
		}
		Log.i(TAG,"New config set");
	}

	public static void setDebugMode(boolean debugMode) {
		GLOBAL_CONFIG.debugMode = debugMode;
	}

	public static Dali create(Context ctx) {
		Precondition.checkNotNull("Provided context must not be null",ctx);

		createCache(ctx);

		Log.i(TAG,"Dali debug mode: "+GLOBAL_CONFIG.debugMode);

		return new Dali(ctx.getApplicationContext());
	}

	private static TwoLevelCache createCache(Context ctx) {
		if(DISK_CACHE_MANAGER == null) {
			DISK_CACHE_MANAGER = new TwoLevelCache(ctx,GLOBAL_CONFIG.globalUseMemoryCache,GLOBAL_CONFIG.globalUseDiskCache,GLOBAL_CONFIG.diskCacheSizeBytes,
					GLOBAL_CONFIG.diskCacheFolderName,GLOBAL_CONFIG.memoryCacheSizeBytes,GLOBAL_CONFIG.debugMode);
		}
		return DISK_CACHE_MANAGER;
	}

	public static ExecutorManager getExecutorManager() {
		if(EXECUTOR_MANAGER == null) {
			EXECUTOR_MANAGER = new ExecutorManager(GLOBAL_CONFIG.maxBlurWorkerThreads);
		}
		return EXECUTOR_MANAGER;
	}

	private ContextWrapper contextWrapper;

	private Dali(Context ctx) {
		contextWrapper = new ContextWrapper(ctx);
	}

	public BlurBuilder load(Bitmap bitmap) {
		return new BlurBuilder(contextWrapper, new ImageReference(bitmap),DISK_CACHE_MANAGER);
	}

	public BlurBuilder load(int resId) {
		return new BlurBuilder(contextWrapper, new ImageReference(resId),DISK_CACHE_MANAGER);
	}

	public BlurBuilder load(InputStream inputStream) {
		return new BlurBuilder(contextWrapper, new ImageReference(inputStream),DISK_CACHE_MANAGER);
	}

	public BlurBuilder load(View view) {
		return new BlurBuilder(contextWrapper, new ImageReference(view),DISK_CACHE_MANAGER);
	}

	public BlurBuilder load(File file) {
		checkFile(file);
		return new BlurBuilder(contextWrapper, new ImageReference(file),DISK_CACHE_MANAGER);
	}

	public BlurBuilder load(URI uri) {
		return load(new File(uri));
	}

	public BlurBuilder load(String path) {
		return load(new File(path));
	}

	private void checkFile(File file) {
		String errMsg = null;
		if(file == null) {
			errMsg = "file object is null";
		} else if(!file.exists()) {
			errMsg = "file does not exist";
		} else if(!file.isFile()) {
			errMsg = "is not a file";
		}

		if(errMsg != null) {
			throw new IllegalArgumentException("Could not load file "+file+": "+errMsg);
		}
	}

	public LiveBlurBuilder liveBlur(View unblurredContentView, View blurOntoView, View... blurOntoViewMore) {
		List<View> viewList = new ArrayList<View>();
		viewList.add(blurOntoView);
		viewList.addAll(Arrays.asList(blurOntoViewMore));
		return new LiveBlurBuilder(contextWrapper,unblurredContentView,viewList);
	}
}
