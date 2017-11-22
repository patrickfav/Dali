package at.favre.lib.dali.builder.animation;

import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;

import at.favre.lib.dali.builder.BuilderDefaults;
import at.favre.lib.dali.util.BuilderUtil;

/**
 * Created by PatrickF on 02.06.2014.
 */
public class BlurKeyFrameAnimationBuilder {
    private int startDownSample = 2;
    private int endDownSample = 8;
    private int framesCount = 4;
    private int endBlurRadius = BuilderDefaults.BLUR_RADIUS;
    private float endBrightness = 0;
    private long durationOfWholeAnimationMs = 800;
    private TimeInterpolator interpolator = new LinearInterpolator();

    public BlurKeyFrameAnimationBuilder() {
    }

    public BlurKeyFrameAnimationBuilder keyFrames(int framesCount) {
        this.framesCount = framesCount;
        return this;
    }

    public BlurKeyFrameAnimationBuilder startDownSample(int startDownSample) {
        this.startDownSample = startDownSample;
        return this;
    }

    public BlurKeyFrameAnimationBuilder endDownSample(int endDownSample) {
        this.endDownSample = endDownSample;
        return this;
    }

    public BlurKeyFrameAnimationBuilder blurRadius(int blurRadius) {
        BuilderUtil.checkBlurRadiusPrecondition(blurRadius);
        this.endBlurRadius = blurRadius;
        return this;
    }

    public BlurKeyFrameAnimationBuilder brightness(int brightness) {
        this.endBrightness = brightness;
        return this;
    }

    public BlurKeyFrameAnimationBuilder duration(long milliseconds) {
        this.durationOfWholeAnimationMs = milliseconds;
        return this;
    }

    public BlurKeyFrameAnimationBuilder interpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public BlurKeyFrameManager build() {
        BlurKeyFrameManager man = new BlurKeyFrameManager();

//        int durationPerFrame = (int) ((float) durationOfWholeAnimationMs / (float) framesCount);
//        int radiusDownScalePerFrame = endDownSample - startDownSample * endBlurRadius;
//        int radiusIncrement = (int) ((float) endBlurRadius / (float) keyFrames);
//        int brightnessIncrement = 0;
//        if (endBrightness != 0) {
//            brightnessIncrement = (int) ((float) endBrightness / (float) keyFrames);
//            ;
//        }
//        for (int i = 0; i < keyFrames; i++) {
//            man.addKeyFrame(new BlurKeyFrame(inSampleSize, radiusIncrement * (i + 1), brightnessIncrement * (i + 1), durationPerFrame));
//        }

        return man;
    }

}
