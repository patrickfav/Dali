package at.favre.lib.dali.blur.algorithms;

import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import at.favre.lib.dali.blur.IBlur;

/**
 * Simple example of ScriptIntrinsicBlur Renderscript gaussion blur.
 * In production always use this algorithm as it is the fastest on Android.
 */
public class RenderScriptGaussianBlur implements IBlur {
    private RenderScript rs;

    public RenderScriptGaussianBlur(RenderScript rs) {
        this.rs = rs;
    }

    @Override
    public Bitmap blur(int radius, Bitmap bitmapOriginal) {
        final Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmapOriginal);
        return bitmapOriginal;
    }
}
