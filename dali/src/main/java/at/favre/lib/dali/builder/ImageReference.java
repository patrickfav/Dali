package at.favre.lib.dali.builder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.File;
import java.io.InputStream;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class ImageReference {
	private Integer resId;
	private InputStream inputStream;
	private Bitmap bitmap;
	private File fileToBitmap;

	private BitmapFactory.Options decoderOptions;

	public ImageReference(int resId) {
		this.resId = resId;
	}

	public ImageReference(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ImageReference(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public ImageReference(File fileToBitmap) {
		this.fileToBitmap = fileToBitmap;
	}

	public void setDecoderOptions(BitmapFactory.Options decoderOptions) {
		this.decoderOptions = decoderOptions;
	}

	public Bitmap synchronouslyLoadBitmap(Resources resources) {
		if(bitmap != null) {
			return bitmap;
		} else if(resId != null) {
			return BitmapFactory.decodeResource(resources, resId, decoderOptions);
		} else if(fileToBitmap != null) {
			return BitmapFactory.decodeFile(fileToBitmap.getAbsolutePath(), decoderOptions);
		} else if(inputStream != null) {
			return BitmapFactory.decodeStream(inputStream, null,decoderOptions);
		}
		throw new IllegalStateException("No image resource was set");
	}

	public Point measureImage(Resources resources) {
		BitmapFactory.Options justBoundsOptions = new BitmapFactory.Options();
		justBoundsOptions.inJustDecodeBounds = true;

		if(bitmap != null) {
			return new Point(bitmap.getWidth(),bitmap.getHeight());
		} else if(resId != null) {
			BitmapFactory.decodeResource(resources, resId, justBoundsOptions);
		} else if(fileToBitmap != null) {
			BitmapFactory.decodeFile(fileToBitmap.getAbsolutePath(), justBoundsOptions);
		} else if(inputStream != null) {
			BitmapFactory.decodeStream(inputStream, null,justBoundsOptions);
		}
		float scale = (float) justBoundsOptions.inTargetDensity / justBoundsOptions.inDensity;
		return new Point((int) (justBoundsOptions.outWidth * scale + 0.5f),(int) (justBoundsOptions.outHeight * scale + 0.5f));
	}
}
