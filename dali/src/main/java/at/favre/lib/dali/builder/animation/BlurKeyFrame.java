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
	private final long duration;

	private Bitmap blurredImage;
	private Bitmap original;

	public BlurKeyFrame(int inSampleSize, int blurRadius, float brightness, long duration, Bitmap original) {
		this.inSampleSize = inSampleSize;
		this.blurRadius = blurRadius;
		this.brightness = brightness;
		this.duration = duration;
		this.original = original;
	}

	public Bitmap prepareFrame(Dali dali) {
		return blurredImage = dali.load(original).downScale(inSampleSize).blurRadius(blurRadius).brightness(brightness).getAsBitmap();
	}

	public long getDuration() {
		return duration;
	}
}
