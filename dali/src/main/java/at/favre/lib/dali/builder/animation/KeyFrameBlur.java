package at.favre.lib.dali.builder.animation;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;

import java.util.List;

import at.favre.lib.dali.Dali;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class KeyFrameBlur extends TransitionDrawable{

	public KeyFrameBlur(Dali dali, Bitmap original, List<KeyFrameConfig> frameConfigList) {
		super(createKeyFrames(dali,original,frameConfigList));
	}

	private static BitmapDrawable[] createKeyFrames(Dali dali, Bitmap original, List<KeyFrameConfig> frameConfigList) {
		BitmapDrawable[] drawables = new BitmapDrawable[frameConfigList.size()];

		int i =0;
		for (KeyFrameConfig keyFrameConfig : frameConfigList) {
			drawables[i] = dali.load(original).downScale(keyFrameConfig.inSampleSize).blurRadius(keyFrameConfig.blurRadius).copyBitmapBeforeProcess(true).reScaleIfDownscaled().get();
			i++;
		}
		return drawables;
	}

	public static class KeyFrameConfig {
		private int inSampleSize;
		private int blurRadius;

		public KeyFrameConfig(int inSampleSize, int blurRadius) {
			this.inSampleSize = inSampleSize;
			this.blurRadius = blurRadius;
		}

		public int getInSampleSize() {
			return inSampleSize;
		}

		public int getBlurRadius() {
			return blurRadius;
		}
	}
}
