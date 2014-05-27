package at.favre.lib.dali.builder.img;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * This will change the brightness of a bitmap. It utilizes
 * a convolve matrix algorithm powered by renderscript (=native & fast)
 *
 * Brightness values are from -100 to +900
 */
public class BrightnessProcessor implements IBitmapProcessor {
	private RenderScript rs;
	private float brightness;

	public BrightnessProcessor(RenderScript rs, float brightness) {
		this.rs = rs;
		this.brightness = brightness;
	}

	@Override
	public Bitmap manipulate(Bitmap bitmapOriginal) {
		if(brightness != 0) {
			Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
			final Allocation output = Allocation.createTyped(rs, input.getType());
			final ScriptIntrinsicConvolve3x3 script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
			script.setCoefficients(createBrightnessKernel(brightness));
			script.setInput(input);
			script.forEach(output);
			output.copyTo(bitmapOriginal);
		}
		return bitmapOriginal;
	}

	@Override
	public String getProcessorTag() {
		return this.getClass().getSimpleName()+": "+brightness;
	}

	private float[] createBrightnessKernel(float brightness) {
		float kernelElement = 1.f / 9.f; //get average
		kernelElement += kernelElement * (brightness / 100.f); //add or subtract from the average to brighten or darken
		kernelElement = Math.max(Math.min(1,kernelElement),0); // normalize to max/min values

		float [] brightnessKernel = new float[9];

		for (int i = 0; i < 9; i++) {
			brightnessKernel[i] = kernelElement;
		}

		return brightnessKernel;
	}
}
