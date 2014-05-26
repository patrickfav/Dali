package at.favre.lib.dali.builder.img;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * Created by PatrickF on 27.05.2014.
 */
public class BrightnessManipulator implements IManipulator {
	private RenderScript rs;
	private float brightness;

	public BrightnessManipulator(RenderScript rs, float brightness) {
		this.rs = rs;
		this.brightness = brightness;
	}

	@Override
	public Bitmap manipulate(Bitmap bitmapOriginal) {
		Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
		final Allocation output = Allocation.createTyped(rs, input.getType());
		final ScriptIntrinsicConvolve3x3 script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
		script.setCoefficients(createBrightnessKernel(brightness));
		script.setInput(input);
		script.forEach(output);
		output.copyTo(bitmapOriginal);
		return bitmapOriginal;
	}

	private float[] createBrightnessKernel(float brightness) {
		float kernelElement = 1.f / 9.f;
		kernelElement += kernelElement * (brightness / 100.f);
		kernelElement = Math.max(Math.min(1,kernelElement),0);

		float [] brightnessKernel = new float[9];

		for (int i = 0; i < 9; i++) {
			brightnessKernel[i] = kernelElement;
		}

		return brightnessKernel;
	}
}
