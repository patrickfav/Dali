package at.favre.lib.dali.builder.live;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.List;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.builder.ABuilder;
import at.favre.lib.dali.builder.ContextWrapper;
import at.favre.lib.dali.builder.blur.BlurBuilder;
import at.favre.lib.dali.util.BuilderUtil;

/**
 * Builder for creating a {@link at.favre.lib.dali.builder.live.LiveBlurWorker}
 */
public class LiveBlurBuilder extends ABuilder {
    private static final String TAG = BlurBuilder.class.getSimpleName();

    private LiveBlurData data;

    protected static class LiveBlurData extends ABuilder.Data {
        public boolean silentFail = false;
        public int downSampleSize = 8;
        public View rootView;
        public List<View> viewsToBlurOnto;
        public Bitmap.Config config = Bitmap.Config.ARGB_8888;
    }

    public LiveBlurBuilder(ContextWrapper contextWrapper, View rootView, List<View> viewsToBlurOnto) {
        data = new LiveBlurData();
        data.rootView = rootView;
        data.viewsToBlurOnto = viewsToBlurOnto;
        data.contextWrapper = contextWrapper;
        data.blurAlgorithm = BuilderUtil.getIBlurAlgorithm(EBlurAlgorithm.RS_GAUSS_FAST, contextWrapper);
    }

    public LiveBlurBuilder downScale(int downSampleSize) {
        data.downSampleSize = downSampleSize;
        return this;
    }

    /**
     * @param blurRadius the views use to blur the view, default is {@link at.favre.lib.dali.builder.BuilderDefaults#BLUR_RADIUS};
     * @throws java.lang.IllegalStateException if blurradius not in range [{@link at.favre.lib.dali.builder.BuilderDefaults#BLUR_RADIUS_MIN},{@link at.favre.lib.dali.builder.BuilderDefaults#BLUR_RADIUS_MAX}}
     */
    public LiveBlurBuilder blurRadius(int blurRadius) {
        BuilderUtil.checkBlurRadiusPrecondition(blurRadius);
        data.blurRadius = blurRadius;
        return this;
    }

    /**
     * When this is called, every exception in the updateBlur method
     * will only print to the log and continue. Use this only in production,
     * otherwise you might miss important errors.
     */
    public LiveBlurBuilder silentFail() {
        data.silentFail = true;
        return this;
    }

    public LiveBlurWorker assemble() {
        return assemble(false);
    }

    public LiveBlurWorker assemble(boolean immediatelyBlur) {
        final LiveBlurWorker worker = new LiveBlurWorker(data);

        if (immediatelyBlur) {
            //this is a hack so that it will be immediately blurred, ViewTreeObserver does not work always
            Handler handler = new Handler(Looper.getMainLooper());
            for (int i = 1; i < 5; i++) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        worker.updateBlurView();
                    }
                }, 300 * i);
            }
        }
        return worker;
    }

}
