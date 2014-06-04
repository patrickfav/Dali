package at.favre.lib.dali.builder.processor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.RSRuntimeException;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import at.favre.lib.dali.R;
import at.favre.lib.dali.ScriptC_brightness;

/**
 * This will change the brightness of a bitmap.
 *
 * Brightness values are from -100 (black) to 100 (white)
 */
public class RenderscriptBrightnessProcessor implements IBitmapProcessor {
	private RenderScript rs;
	private float brightness;
    private Resources res;

	public RenderscriptBrightnessProcessor(RenderScript rs, float brightness, Resources res) {
		this.rs = rs;
		this.brightness = brightness;
        this.res = res;
    }

	@Override
	public Bitmap manipulate(Bitmap bitmapOriginal) {
		if(brightness != 0) {
            try {
                Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
                final Allocation output = Allocation.createTyped(rs, input.getType());
                ScriptC_brightness mScript = new ScriptC_brightness(rs,res, R.raw.brightness);
                mScript.invoke_setBright(brightness);
                mScript.forEach_brightness(input, output);
                output.copyTo(bitmapOriginal);
            } catch (RSRuntimeException e) {
                //fallback
            }
		}
		return bitmapOriginal;
	}

	@Override
	public String getProcessorTag() {
		return this.getClass().getSimpleName()+": "+brightness;
	}
}
