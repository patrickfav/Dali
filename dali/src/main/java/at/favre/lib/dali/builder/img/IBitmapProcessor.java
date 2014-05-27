package at.favre.lib.dali.builder.img;

import android.graphics.Bitmap;

/**
 * Created by PatrickF on 27.05.2014.
 */
public interface IBitmapProcessor {

	public Bitmap manipulate(Bitmap original);

	public String getProcessorTag();
}
