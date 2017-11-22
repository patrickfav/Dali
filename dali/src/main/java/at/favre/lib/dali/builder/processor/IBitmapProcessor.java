package at.favre.lib.dali.builder.processor;

import android.graphics.Bitmap;

/**
 * Created by PatrickF on 27.05.2014.
 */
public interface IBitmapProcessor {

    Bitmap manipulate(Bitmap original);

    String getProcessorTag();
}
