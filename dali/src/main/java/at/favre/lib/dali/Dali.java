package at.favre.lib.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.favre.lib.dali.builder.ContextWrapper;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.blur.BlurBuilder;
import at.favre.lib.dali.builder.live.LiveBlurBuilder;
import at.favre.lib.dali.util.Precondition;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class Dali {
	private static DiskCacheManager DISK_CACHE_MANAGER;

	public static Dali create(Context ctx) {
		return create(ctx,false);
	}
	public static Dali create(Context ctx, boolean debugMode) {
		Precondition.checkNotNull("Provided context must not be null",ctx);
		DISK_CACHE_MANAGER = new DiskCacheManager(ctx);
		return new Dali(ctx.getApplicationContext(),debugMode);
	}


	private ContextWrapper contextWrapper;
	private boolean debugMode;

	private Dali(Context ctx, boolean debugMode) {
		this.debugMode = debugMode;
		contextWrapper = new ContextWrapper(ctx);
	}

	public BlurBuilder load(Bitmap bitmap) {
		return new BlurBuilder(contextWrapper, new ImageReference(bitmap),DISK_CACHE_MANAGER,debugMode);
	}

	public BlurBuilder load(int resId) {
		return new BlurBuilder(contextWrapper, new ImageReference(resId),DISK_CACHE_MANAGER,debugMode);
	}

	public BlurBuilder load(InputStream inputStream) {
		return new BlurBuilder(contextWrapper, new ImageReference(inputStream),DISK_CACHE_MANAGER,debugMode);
	}

	public BlurBuilder load(File file) {
		checkFile(file);
		return new BlurBuilder(contextWrapper, new ImageReference(file),DISK_CACHE_MANAGER,debugMode);
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
		return new LiveBlurBuilder(contextWrapper,unblurredContentView,viewList,debugMode);
	}
}
