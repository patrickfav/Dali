package at.favre.lib.dali.builder.processor;

import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * This will change the brightness of a bitmap. It utilizes
 * a convolve matrix algorithm powered by renderscript (=native and fast)
 * <p>
 * Brightness values are from -100 (black) to 1000 and more
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
        if (brightness != 0) {
            Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicConvolve3x3 script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
            script.setCoefficients(createBrightnessKernel2(brightness));
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmapOriginal);
        }
        return bitmapOriginal;
    }

    @Override
    public String getProcessorTag() {
        return this.getClass().getSimpleName() + ": " + brightness;
    }

    private float[] createBrightnessKernel(float brightness) {
        float kernelElement;
        if (brightness < 0) {
            kernelElement = 1f - Math.abs(brightness) / 100f;
        } else {
            kernelElement = 1f + Math.abs(brightness) / 100f;
        }

        kernelElement = Math.max(Math.min(100, kernelElement), 0); // normalize to max/min values

        float[] brightnessKernel = new float[9];

        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                brightnessKernel[i] = kernelElement;
            } else {
                brightnessKernel[i] = 0;
            }
        }

        return brightnessKernel;
    }

    private float[] createBrightnessKernel2(float brightness) {
        float kernelElement = 1.f / 9.f; //get average
        kernelElement += kernelElement * (brightness / 100.f); //add or subtract from the average to brighten or darken
        kernelElement = Math.max(Math.min(1, kernelElement), 0); // normalize to max/min values

        float[] brightnessKernel = new float[9];

        for (int i = 0; i < 9; i++) {
            brightnessKernel[i] = kernelElement;
        }

        return brightnessKernel;
    }
}
