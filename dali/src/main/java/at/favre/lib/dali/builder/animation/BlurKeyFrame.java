package at.favre.lib.dali.builder.animation;

import android.graphics.Bitmap;

import at.favre.lib.dali.Dali;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class BlurKeyFrame {
	private final int inSampleSize;
	private final int blurRadius;
	private final float brightness;
	private final int duration;

	public BlurKeyFrame(int inSampleSize, int blurRadius, float brightness, int duration) {
		this.inSampleSize = inSampleSize;
		this.blurRadius = blurRadius;
		this.brightness = brightness;
		this.duration = duration;
	}

	protected Bitmap prepareFrame(Bitmap original, Dali dali) {
		return dali.load(original).downScale(inSampleSize).blurRadius(blurRadius).brightness(brightness).reScaleIfDownscaled().getAsBitmap();
	}

	public int getDuration() {
		return duration;
	}
}
