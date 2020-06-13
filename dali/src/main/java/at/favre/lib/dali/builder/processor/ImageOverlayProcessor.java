package at.favre.lib.dali.builder.processor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlend;

import at.favre.lib.dali.R;

/**
 * Created by PatrickF on 27.05.2014.
 */
public class ImageOverlayProcessor implements IBitmapProcessor {
    private RenderScript rs;
    private Resources res;

    public ImageOverlayProcessor(RenderScript rs, Resources res) {
        this.rs = rs;
        this.res = res;
    }

    @Override
    public Bitmap manipulate(Bitmap original) {
        final Allocation input1 = Allocation.createFromBitmap(rs, original);
        final Allocation input2 = Allocation.createFromBitmap(rs, getBlendImage(original));
        final ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(rs, Element.U8_4(rs));
        blendScript.forEachSrcOver(input2, input1);
        input1.copyTo(original);
        return original;
    }

    public Bitmap getBlendImage(Bitmap original) {
        Bitmap frost = BitmapFactory.decodeResource(res, R.drawable.frost4);
        return Bitmap.createScaledBitmap(frost, original.getWidth(), original.getHeight(), true);
    }

    @Override
    public String getProcessorTag() {
        return this.getClass().getSimpleName();
    }
}
