package at.favre.lib.dali.blur.algorithms;

import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicConvolve5x5;

import at.favre.lib.dali.blur.BlurKernels;
import at.favre.lib.dali.blur.IBlur;

/**
 * This is a convolve matrix based blur algorithms powered by Renderscript's ScriptIntrinsicConvolve class. This uses a gaussian kernel.
 * Instead of radius it uses passes, so a radius parameter of 16 makes the convolve algorithm applied 16 times onto the image.
 */
public class RenderScriptGaussian5x5Blur implements IBlur {
    private RenderScript rs;

    public RenderScriptGaussian5x5Blur(RenderScript rs) {
        this.rs = rs;
    }

    @Override
    public Bitmap blur(int radius, Bitmap bitmapOriginal) {
        Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicConvolve5x5 script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs));
        script.setCoefficients(BlurKernels.GAUSSIAN_5x5);
        for (int i = 0; i < radius; i++) {
            script.setInput(input);
            script.forEach(output);
            input = output;
        }
        output.copyTo(bitmapOriginal);
        return bitmapOriginal;
    }
}
