package at.favre.lib.dali.blur.algorithms;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;

import at.favre.lib.dali.ScriptC_stackblur;
import at.favre.lib.dali.blur.IBlur;

/**
 * by kikoso
 * from https://github.com/kikoso/android-stackblur/blob/master/StackBlur/src/blur.rs
 */
public class RenderScriptStackBlur implements IBlur {
    private RenderScript _rs;
    private Context ctx;

    public RenderScriptStackBlur(RenderScript rs, Context ctx) {
        this.ctx = ctx;
        this._rs = rs;
    }

    @Override
    public Bitmap blur(int radius, Bitmap blurred) {
        int width = blurred.getWidth();
        int height = blurred.getHeight();

        ScriptC_stackblur blurScript = new ScriptC_stackblur(_rs);
        Allocation inAllocation = Allocation.createFromBitmap(_rs, blurred);

        blurScript.set_gIn(inAllocation);
        blurScript.set_width(width);
        blurScript.set_height(height);
        blurScript.set_radius(radius);

        int[] row_indices = new int[height];
        for (int i = 0; i < height; i++) {
            row_indices[i] = i;
        }

        Allocation rows = Allocation.createSized(_rs, Element.U32(_rs), height, Allocation.USAGE_SCRIPT);
        rows.copyFrom(row_indices);

        row_indices = new int[width];
        for (int i = 0; i < width; i++) {
            row_indices[i] = i;
        }

        Allocation columns = Allocation.createSized(_rs, Element.U32(_rs), width, Allocation.USAGE_SCRIPT);
        columns.copyFrom(row_indices);

        blurScript.forEach_blur_h(rows);
        blurScript.forEach_blur_v(columns);
        inAllocation.copyTo(blurred);

        return blurred;

    }
}
