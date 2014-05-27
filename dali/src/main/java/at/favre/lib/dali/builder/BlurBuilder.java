package at.favre.lib.dali.builder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.RenderScript;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.blur.IBlur;
import at.favre.lib.dali.blur.algorithms.BoxBlur;
import at.favre.lib.dali.blur.algorithms.GaussianFastBlur;
import at.favre.lib.dali.blur.algorithms.IgnoreBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptBox5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussian5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussianBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptStackBlur;
import at.favre.lib.dali.blur.algorithms.StackBlur;
import at.favre.lib.dali.builder.img.BrightnessProcessor;
import at.favre.lib.dali.builder.img.ContrastProcessor;
import at.favre.lib.dali.builder.img.FrostGlassProcessor;
import at.favre.lib.dali.builder.img.IBitmapProcessor;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class BlurBuilder {
	private final static String TAG = BlurBuilder.class.getSimpleName();

	private static final int MIN_BLUR_RADIUS = 1;
	private static final int MAX_BLUR_RADIUS = 25;

	private Data data;

	protected static class Data {
		protected boolean debugMode = true;
		protected BitmapFactory.Options options = new BitmapFactory.Options();
		protected IBlur blurAlgorithm;
		protected boolean copyBitmapBeforeBlur = false;
		protected int blurRadius=16;
		protected boolean rescaleIfDownscaled = true;
		protected ImageReference imageReference;
		protected ContextWrapper contextWrapper;
		protected List<IBitmapProcessor> preProcessors = new ArrayList<IBitmapProcessor>();
		protected List<IBitmapProcessor> postProcessors = new ArrayList<IBitmapProcessor>();
	}

	public BlurBuilder(ContextWrapper contextWrapper, ImageReference imageReference) {
		data = new Data();
		data.imageReference = imageReference;
		data.contextWrapper = contextWrapper;
		data.blurAlgorithm = new RenderScriptGaussianBlur(data.contextWrapper.getRenderScript());
	}

	public BlurBuilder blurRadius(int radius) {
		checkBlurRadiusPrecondition(radius);
		data.blurRadius = radius;
		return this;
	}

	private void checkBlurRadiusPrecondition(int blurRadius) {
		if(blurRadius < MIN_BLUR_RADIUS ||  blurRadius > MAX_BLUR_RADIUS) {
			throw new IllegalArgumentException("Valid blur radius must be between (inclusive) "+MIN_BLUR_RADIUS+" and "+MAX_BLUR_RADIUS+" found "+blurRadius);
		}
	}

	/**
	 * If this the image bitmap should be copied before blur.
	 *
	 * This will increase memory (RAM) usage while blurring, but
	 * if the bitmap's object is used anywhere else it would
	 * create side effects.
	 *
	 * @param shouldCopy
	 * @return
	 */
	public BlurBuilder copyBitmapBeforeProcess(boolean shouldCopy) {
		data.copyBitmapBeforeBlur = shouldCopy;
		return this;
	}

	public BlurBuilder downScale(int scaleInSample) {
		data.options.inSampleSize = Math.min(Math.max(1, scaleInSample), 16384);
		return this;
	}

	public BlurBuilder reScaleIfDownscaled(boolean rescale) {
		data.rescaleIfDownscaled = rescale;
		return this;
	}

	public BlurBuilder options(BitmapFactory.Options options) {
		data.options =options;
		return this;
	}

	/**
	 * Add custom processor. This will be applied to the
	 * image BEFORE blurring. The order in which this is
	 * calls defines the order the processors are applied.
	 *
	 * @param processor
	 * @return
	 */
	public BlurBuilder addPreProcessor(IBitmapProcessor processor) {
		data.preProcessors.add(processor);
		return this;
	}

	/**
	 * Set brightness to  eg. darken the resulting image for use as background
	 *
	 * @param brightness default is 0, pos values increase brightness, neg. values decrease contrast
	 *                   min value is -100 (which is all black) and max value is 900 (which is all white)
	 * @return
	 */
	public BlurBuilder brightness(float brightness) {
		data.postProcessors.add(new BrightnessProcessor(data.contextWrapper.getRenderScript(),Math.max(Math.min(900.f,brightness),-100.f)));
		return this;
	}

	/**
	 * Change contrast of the image
	 *
	 * @param contrast default is 0, pos values increase contrast, neg. values decrease contrast
	 * @return
	 */
	public BlurBuilder contrast(float contrast) {
		data.postProcessors.add(new ContrastProcessor(data.contextWrapper.getRenderScript(),Math.max(Math.min(1500.f,contrast),-1500.f)));
		return this;
	}

	public BlurBuilder frostedGlass() {
		data.postProcessors.add(new FrostGlassProcessor(data.contextWrapper.getRenderScript(),data.contextWrapper.getContext().getResources()));
		return this;
	}

	/**
	 * Add custom processor. This will be applied to the
	 * image AFTER blurring. The order in which this is
	 * calls defines the order the processors are applied.
	 *
	 * @param processor
	 * @return
	 */
	public BlurBuilder addPostProcessor(IBitmapProcessor processor) {
		data.postProcessors.add(processor);
		return this;
	}

	/**
	 * Sets the blur algorithm.
	 *
	 * NOTE: this probably never is necessary to do except for testing purpose, the default
	 * algorithm, which uses Android's {@link android.support.v8.renderscript.ScriptIntrinsicBlur}
	 * which is the best and fastest you get on Android suffices in nearly every situation
	 *
	 * @param algorithm
	 * @return
	 */
	public BlurBuilder algorithm(EBlurAlgorithm algorithm) {
		RenderScript rs= data.contextWrapper.getRenderScript();
		Context ctx = data.contextWrapper.getContext();

		switch (algorithm) {
			case RS_GAUSS_FAST:
				data.blurAlgorithm =  new RenderScriptGaussianBlur(rs);
			case RS_BOX_5x5:
				data.blurAlgorithm = new RenderScriptBox5x5Blur(rs);
			case RS_GAUSS_5x5:
				data.blurAlgorithm = new RenderScriptGaussian5x5Blur(rs);
			case RS_STACKBLUR:
				data.blurAlgorithm = new RenderScriptStackBlur(rs, ctx);
			case STACKBLUR:
				data.blurAlgorithm = new StackBlur();
			case GAUSS_FAST:
				data.blurAlgorithm = new GaussianFastBlur();
			case BOX_BLUR:
				data.blurAlgorithm = new BoxBlur();
			default:
				data.blurAlgorithm = new IgnoreBlur();
		}

		return this;
	}

	/**
	 * Provide your custom blur implementation
	 *
	 * @param blurAlgorithm
	 * @return
	 */
	public BlurBuilder algorithm(IBlur blurAlgorithm) {
		data.blurAlgorithm = blurAlgorithm;
		return this;
	}

	public Bitmap get() {
		return new BlurWorker(data,null).process();
	}

	public interface TaskFinishedListener {
		public void onBitmapReady(Bitmap manipulatedBitmap);
		public void onError(Throwable t);
	}

}
