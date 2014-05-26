package at.favre.lib.dali.builder;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.blur.algorithms.BoxBlur;
import at.favre.lib.dali.blur.algorithms.GaussianFastBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptBox5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussian5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussianBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptStackBlur;
import at.favre.lib.dali.blur.algorithms.StackBlur;
import at.favre.lib.dali.builder.img.BrightnessManipulator;
import at.favre.lib.dali.builder.img.ContrastManipulator;
import at.favre.lib.dali.util.BenchmarkUtil;
import at.favre.lib.dali.util.BitmapUtil;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class BlurWorker implements Runnable {
	private BlurBuilder builder;
	private boolean enableLog;
	private BlurBuilder.TaskFinishedListener listener;

	public BlurWorker(BlurBuilder builder,BlurBuilder.TaskFinishedListener listener) {
		this(builder,listener,false);
	}

	public BlurWorker(BlurBuilder builder,BlurBuilder.TaskFinishedListener listener,boolean enableLog) {
		this.builder = builder;
		this.enableLog = enableLog;
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			listener.onBitmapReady(process());
		} catch (Throwable t) {
			listener.onError(t);
		}
	}


	public Bitmap process() {
		PerformanceProfiler profiler = new PerformanceProfiler("blur image",enableLog);

		profiler.startTask(0, "load bitmap");
		Bitmap bitmapToWorkWith = builder.getImageReference().syncronouslyLoadBitmap(builder.getContextWrapper().getContext().getResources());
		profiler.endTask(0, "height:" + bitmapToWorkWith.getHeight() + ", width:" + bitmapToWorkWith.getWidth() + ", memory usage " + BenchmarkUtil.getScalingUnitByteSize(BitmapUtil.sizeOf(bitmapToWorkWith)));

		if (builder.isCopyBitmapBeforeBlur()) {
			profiler.startTask(1, "copy bitmap");
			bitmapToWorkWith = bitmapToWorkWith.copy(bitmapToWorkWith.getConfig(), true);
			profiler.endTask(1);
		}

		profiler.startTask(2, "blur with radius " + builder.getBlurRadius() + " and algorithm " + builder.getAlgorithm());
		bitmapToWorkWith = blur(builder.getBlurRadius(), builder.getContextWrapper().getRenderScript(), builder.getContextWrapper().getContext(), bitmapToWorkWith, builder.getAlgorithm());
		profiler.endTask(2);

		if (builder.getContrast() != 0.f) {
			profiler.startTask(3, "change contrast with parameter " + builder.getContrast());
			bitmapToWorkWith = new ContrastManipulator(builder.getContextWrapper().getRenderScript(),builder.getContrast()).manipulate(bitmapToWorkWith);
			profiler.endTask(3);
		}

		if (builder.getBrightness() != 0.f) {
			profiler.startTask(4, "change brightness with parameter " + builder.getBrightness());
			bitmapToWorkWith = new BrightnessManipulator(builder.getContextWrapper().getRenderScript(),builder.getBrightness()).manipulate(bitmapToWorkWith);
			profiler.endTask(4);
		}

		if (builder.isShouldUseFrostedGlassEffect()) {
			profiler.startTask(5, "apply frosted glass effect");
			bitmapToWorkWith = createFrostedGlassEffect(bitmapToWorkWith);
			profiler.endTask(5);
		}

		profiler.printResultToLog();

		return bitmapToWorkWith;
	}

	private Bitmap blur(int radius, RenderScript rs, Context ctx, Bitmap original, EBlurAlgorithm algorithm) {
		switch (algorithm) {
			case RS_GAUSS_FAST:
				return new RenderScriptGaussianBlur(rs).blur(radius, original);
			case RS_BOX_5x5:
				return new RenderScriptBox5x5Blur(rs).blur(radius, original);
			case RS_GAUSS_5x5:
				return new RenderScriptGaussian5x5Blur(rs).blur(radius, original);
			case RS_STACKBLUR:
				return new RenderScriptStackBlur(rs, ctx).blur(radius, original);
			case STACKBLUR:
				return new StackBlur().blur(radius, original);
			case GAUSS_FAST:
				return new GaussianFastBlur().blur(radius, original);
			case BOX_BLUR:
				return new BoxBlur().blur(radius, original);
			default:
				return original;
		}
	}




	private Bitmap createFrostedGlassEffect(Bitmap original) {
		return original; //TODO implement
	}
}
