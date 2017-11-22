package at.favre.lib.dali.builder.processor;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

/**
 * Created by PatrickF on 03.06.2014.
 */
public class RenderScriptColorFilter implements IBitmapProcessor {
    private RenderScript rs;
    private float[] data;

    public RenderScriptColorFilter(RenderScript rs, float[] matrix) {
        this.rs = rs;
        this.data = matrix;
    }

    @Override
    public Bitmap manipulate(Bitmap bitmapOriginal) {
        try {
            Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicColorMatrix mScript = ScriptIntrinsicColorMatrix.create(rs, Element.U8(rs));
            Matrix4f matrix4f = new Matrix4f(data);
            mScript.setColorMatrix(matrix4f);
            output.copyTo(bitmapOriginal);
        } catch (RSRuntimeException e) {
            //fallback
        }
        return bitmapOriginal;
    }

    @Override
    public String getProcessorTag() {
        return this.getClass().getSimpleName() + ": ";
    }
}
