package at.favre.lib.dali;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import at.favre.lib.dali.builder.BlurBuilder;
import at.favre.lib.dali.builder.ContextWrapper;
import at.favre.lib.dali.builder.ImageReference;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class Dali {

	public static Dali create(Context ctx) {
		return new Dali(ctx.getApplicationContext());
	}


	private ContextWrapper contextWrapper;

	private Dali(Context ctx) {
		contextWrapper = new ContextWrapper(ctx);
	}

	public BlurBuilder load(Bitmap bitmap) {
		return new BlurBuilder(contextWrapper, new ImageReference(bitmap));
	}

	public BlurBuilder load(int resId) {
		return new BlurBuilder(contextWrapper, new ImageReference(resId));
	}

	public BlurBuilder load(InputStream inputStream) {
		return new BlurBuilder(contextWrapper, new ImageReference(inputStream));
	}

	public BlurBuilder load(File file) {
		checkFile(file);
		return new BlurBuilder(contextWrapper, new ImageReference(file));
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
}
