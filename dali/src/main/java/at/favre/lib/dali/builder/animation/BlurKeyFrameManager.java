package at.favre.lib.dali.builder.animation;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.Dali;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class BlurKeyFrameManager {

    public static BlurKeyFrameManager createLinearKeyFrames(int keyFrames, int duration, int inSampleSize, int endBlurRadius, int endBrightness) {
        BlurKeyFrameManager man = new BlurKeyFrameManager();

        int durationPerFrame = (int) ((float) duration / (float) keyFrames);
        int radiusIncrement = (int) ((float) endBlurRadius / (float) keyFrames);
        int brightnessIncrement = 0;
        if (endBrightness != 0) {
            brightnessIncrement = (int) ((float) endBrightness / (float) keyFrames);
        }
        for (int i = 0; i < keyFrames; i++) {
            man.addKeyFrame(new BlurKeyFrame(inSampleSize, radiusIncrement * (i + 1), brightnessIncrement * (i + 1), durationPerFrame));
        }

        return man;
    }

    public static BlurKeyFrameManager createLowMemoryKeyframes(int keyFrames, int duration, int startInsampleSize, int endBlurRadius) {
        BlurKeyFrameManager man = new BlurKeyFrameManager();

        int durationPerFrame = (int) ((float) duration / (float) keyFrames);
        int radiusIncrement = (int) ((float) endBlurRadius / (float) keyFrames);
        for (int i = 0; i < keyFrames; i++) {
            man.addKeyFrame(new BlurKeyFrame(startInsampleSize, radiusIncrement * (i + 1), 0, durationPerFrame));
        }

        return man;
    }

    private List<BlurKeyFrame> keyFrames = new ArrayList<BlurKeyFrame>();

    public BlurKeyFrameManager() {
    }

    public void addKeyFrame(BlurKeyFrame frame) {
        keyFrames.add(frame);
    }

    protected KeyFrameData prepareFrames(Context ctx, Bitmap original) {
        return new KeyFrameData(ctx, original, keyFrames);
    }

    public List<BlurKeyFrame> getKeyFrames() {
        return keyFrames;
    }

    public static class KeyFrameData {
        private Bitmap original;
        private List<Bitmap> frames = new ArrayList<Bitmap>();
        private List<BlurKeyFrame> keyFrameConfigList;

        public KeyFrameData(Context ctx, Bitmap original, List<BlurKeyFrame> keyFrames) {
            this.original = original;
            this.keyFrameConfigList = keyFrames;

            Dali dali = Dali.create(ctx);
            frames.add(original);
            for (BlurKeyFrame keyFrame : keyFrames) {
                frames.add(keyFrame.prepareFrame(original, dali));
            }
        }

        public List<BlurKeyFrame> getKeyFrameConfigList() {
            return keyFrameConfigList;
        }

        public List<Bitmap> getFrames() {
            return frames;
        }

        public Bitmap getOriginal() {
            return original;
        }
    }

    @Override
    public String toString() {
        return keyFrames.toString();
    }
}
