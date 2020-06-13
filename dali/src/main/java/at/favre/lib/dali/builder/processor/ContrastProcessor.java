package at.favre.lib.dali.builder.processor;

import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import at.favre.lib.dali.ScriptC_contrast;

/**
 * This will change the contrast of a bitmap.
 * It uses a renderscript (contrast.rs)
 * <p>
 * contrast values are from -100 to +100
 */
public class ContrastProcessor implements IBitmapProcessor {
    private RenderScript rs;
    private float contrast;

    public ContrastProcessor(RenderScript rs, float contrast) {
        this.rs = rs;
        this.contrast = contrast;
    }

    @Override
    public Bitmap manipulate(Bitmap original) {
        if (contrast != 0) {
            Allocation input = Allocation.createFromBitmap(rs, original);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptC_contrast mScript = new ScriptC_contrast(rs);
            mScript.invoke_setBright(contrast);
            mScript.forEach_contrast(input, output);
            output.copyTo(original);
        }
        return original;
    }

    @Override
    public String getProcessorTag() {
        return this.getClass().getSimpleName() + ": " + contrast;
    }
}
