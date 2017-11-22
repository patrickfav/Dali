package at.favre.lib.dali.builder.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

/**
 * Created by PatrickF on 27.05.2014.
 */
public class ColorFilterProcessor implements IBitmapProcessor {
    private int colorResId;
    private PorterDuff.Mode porterDuffMode;

    public ColorFilterProcessor(int colorResId, PorterDuff.Mode porterDuffMode) {
        this.colorResId = colorResId;
        this.porterDuffMode = porterDuffMode;
    }

    @Override
    public Bitmap manipulate(Bitmap original) {
        if (!original.isMutable()) {
            original = original.copy(original.getConfig(), true);
        }
        Paint p = new Paint(colorResId);
        ColorFilter filter = new PorterDuffColorFilter(colorResId, porterDuffMode);
        p.setColorFilter(filter);

        Canvas c = new Canvas(original);
        c.drawBitmap(original, 0, 0, p);

        return original;
    }

    @Override
    public String getProcessorTag() {
        return this.getClass().getSimpleName() + ": " + colorResId;
    }
}
