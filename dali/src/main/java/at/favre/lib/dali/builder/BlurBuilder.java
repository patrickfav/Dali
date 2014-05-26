package at.favre.lib.dali.builder;

import android.graphics.Bitmap;

import at.favre.lib.dali.blur.EBlurAlgorithm;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class BlurBuilder {
	private final static String TAG = BlurBuilder.class.getSimpleName();

	private static final int MIN_BLUR_RADIUS = 1;
	private static final int MAX_BLUR_RADIUS = 25;

	private boolean debugMode = true;

	private boolean shouldUseFrostedGlassEffect = false;
	private float contrast = 0.f;
	private float brightness = 0.f;
	private EBlurAlgorithm algorithm = EBlurAlgorithm.RS_GAUSS_FAST;
	private boolean copyBitmapBeforeBlur = false;
	private int blurRadius=16;
	private ImageReference imageReference;
	private ContextWrapper contextWrapper;

	public BlurBuilder(ContextWrapper contextWrapper, ImageReference imageReference) {
		this.imageReference = imageReference;
		this.contextWrapper = contextWrapper;
	}

	public BlurBuilder blurRadius(int radius) {
		checkBlurRadiusPrecondition(radius);
		this.blurRadius = radius;
		return this;
	}

	private void checkBlurRadiusPrecondition(int blurRadius) {
		if(blurRadius < MIN_BLUR_RADIUS ||  blurRadius > MAX_BLUR_RADIUS) {
			throw new IllegalArgumentException("Valid blur radius must be between (inclusive) "+MIN_BLUR_RADIUS+" and "+MAX_BLUR_RADIUS+" found "+blurRadius);
		}
	}

	public BlurBuilder frostedGlass(boolean shouldEnableEffect) {
		shouldUseFrostedGlassEffect = shouldEnableEffect;
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
		this.brightness = Math.max(Math.min(900.f,brightness),-100.f);
		return this;
	}


	/**
	 * Change contrast of the image
	 *
	 * @param contrast default is 0, pos values increase contrast, neg. values decrease contrast
	 * @return
	 */
	public BlurBuilder contrast(float contrast) {
		this.contrast = Math.max(Math.min(1500.f,contrast),-1500.f);
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
		this.algorithm = algorithm;
		return this;
	}

	/**
	 * If this the image bitmap should be copied before blur.
	 *
	 * This will increase memory (RAM) usage while blurring, but
	 * if the bitmap's pointer is used anywhere else it would be
	 * affected of course.
	 *
	 * @param shouldCopy
	 * @return
	 */
	public BlurBuilder copyBitmapBeforeBlur(boolean shouldCopy) {
		this.copyBitmapBeforeBlur = shouldCopy;
		return this;
	}


	protected boolean isShouldUseFrostedGlassEffect() {
		return shouldUseFrostedGlassEffect;
	}

	protected float getContrast() {
		return contrast;
	}

	protected EBlurAlgorithm getAlgorithm() {
		return algorithm;
	}

	protected boolean isCopyBitmapBeforeBlur() {
		return copyBitmapBeforeBlur;
	}

	protected int getBlurRadius() {
		return blurRadius;
	}

	protected ImageReference getImageReference() {
		return imageReference;
	}

	protected ContextWrapper getContextWrapper() {
		return contextWrapper;
	}

	protected float getBrightness() {
		return brightness;
	}

	public Bitmap get() {
		return new BlurWorker(this,null,debugMode).process();
	}

	public interface TaskFinishedListener {
		public void onBitmapReady(Bitmap manipulatedBitmap);
		public void onError(Throwable t);
	}

}
