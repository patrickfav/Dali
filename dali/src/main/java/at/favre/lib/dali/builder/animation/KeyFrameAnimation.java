package at.favre.lib.dali.builder.animation;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by PatrickF on 03.06.2014.
 */
public class KeyFrameAnimation {
    private List<KeyFrame> keyFrameList;

    public interface KeyFrame {
        long getDurationMs();

        Bitmap createBitmap();
    }
}
