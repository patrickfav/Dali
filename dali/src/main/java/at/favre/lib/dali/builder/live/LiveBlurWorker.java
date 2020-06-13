package at.favre.lib.dali.builder.live;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.exception.LiveBlurWorkerException;
import at.favre.lib.dali.util.BuilderUtil;

/**
 * This is contains the business logic for the live blur feature.
 * It is optimized to reuse resources for fast and continuous re-blurring.
 */
public class LiveBlurWorker {
    private static final String TAG = LiveBlurWorker.class.getSimpleName();
    private static final int BLUR_ROUNDS_PER_UPDATE = 75;

    private AtomicBoolean isWorking = new AtomicBoolean(false);
    private AtomicInteger blursLeft = new AtomicInteger(0);

    private Bitmap dest;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable blurRunnable = new BlurRunnable();

    private LiveBlurBuilder.LiveBlurData data;

    public LiveBlurWorker(LiveBlurBuilder.LiveBlurData data) {
        this.data = data;
    }

    public boolean updateBlurView() {
        try {
            Dali.logD(TAG, "update");
            if (!BuilderUtil.isOnUiThread()) {
                Dali.logD(TAG, "Not on ui thread");
                return false;
            }

            if (data.rootView == null || data.viewsToBlurOnto.isEmpty()) {
                Dali.logD(TAG, "Views not set");
                return false;
            }

            if (data.viewsToBlurOnto.get(0).getWidth() == 0 || data.viewsToBlurOnto.get(0).getHeight() == 0) {
                Dali.logD(TAG, "Views not ready to be blurred");
                return false;
            }

            if (blursLeft.get() < BLUR_ROUNDS_PER_UPDATE) {
                blursLeft.addAndGet(BLUR_ROUNDS_PER_UPDATE);
            }

            if (!isWorking.get()) {
                handler.post(blurRunnable);
                return true;
            } else {
                Dali.logD(TAG, "Skip blur frame, already in blur");
            }
        } catch (Throwable t) {
            isWorking.set(false);
            if (data.silentFail) {
                Log.e(TAG, "Could not create blur view", t);
            } else {
                throw new LiveBlurWorkerException("Error while updating the live blur", t);
            }
        }
        return false;
    }

    private class BlurRunnable implements Runnable {
        @Override
        public void run() {
            isWorking.compareAndSet(false, true);
            dest = BuilderUtil.drawViewToBitmap(dest, data.rootView, data.downSampleSize, data.config);

            for (View view : data.viewsToBlurOnto) {
                Drawable d = new BitmapDrawable(data.contextWrapper.getResources(), data.blurAlgorithm.blur(data.blurRadius, crop(dest.copy(dest.getConfig(), true), view, data.downSampleSize)));
                ViewCompat.setBackground(view, d);
            }

            int left = blursLeft.decrementAndGet();
            if (left > 0) {
                handler.post(this);
            } else {
                isWorking.compareAndSet(true, false);
            }
        }
    }

    /**
     * crops the srcBmp with the canvasView bounds and returns the cropped bitmap
     */
    private static Bitmap crop(Bitmap srcBmp, View canvasView, int downsampling) {
        float scale = 1f / downsampling;
        return Bitmap.createBitmap(
                srcBmp,
                (int) Math.floor((ViewCompat.getX(canvasView)) * scale),
                (int) Math.floor((ViewCompat.getY(canvasView)) * scale),
                (int) Math.floor((canvasView.getWidth()) * scale),
                (int) Math.floor((canvasView.getHeight()) * scale)
        );
    }
}
