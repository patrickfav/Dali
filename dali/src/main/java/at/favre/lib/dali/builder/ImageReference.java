package at.favre.lib.dali.builder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

	public Bitmap syncronouslyLoadBitmap(Resources resources) {
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
}
