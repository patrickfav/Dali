package at.favre.lib.dali.builder.live;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class LiveBlurWorker {
	private final static String TAG = LiveBlurWorker.class.getSimpleName();

	private AtomicBoolean isWorking = new AtomicBoolean(false);
	private Bitmap dest;

	private LiveBlurBuilder.LiveBlurData data;

	public LiveBlurWorker(LiveBlurBuilder.LiveBlurData data) {
		this.data = data;
	}

	public boolean updateBlurView() {
		try {
			if(data.rootView == null || data.viewsToBlurOnto.isEmpty()) {
				BuilderUtil.logDebug(TAG, "Views not set", Dali.getConfig().debugMode);
				return false;
			}

			if( data.viewsToBlurOnto.get(0).getWidth() == 0 || data.viewsToBlurOnto.get(0).getHeight() == 0) {
				BuilderUtil.logDebug(TAG, "Views not ready to be blurred", Dali.getConfig().debugMode);
				return false;
			}

			if (!isWorking.get()) {
				isWorking.compareAndSet(false, true);

				dest = drawViewToBitmap(dest, data.rootView, data.inSampleSize,data.config);

				for (View view : data.viewsToBlurOnto) {
					Drawable d = new BitmapDrawable(data.contextWrapper.getResources(), data.blurAlgorithm.blur(data.blurRadius,crop(dest.copy(dest.getConfig(), true), view, data.inSampleSize)));
					LegacySDKUtil.setViewBackground(view, d);
				}
				isWorking.compareAndSet(true, false);
				return true;
			} else {
				BuilderUtil.logDebug(TAG, "Skip blur frame, already in blur", Dali.getConfig().debugMode);
			}
		} catch (Throwable t) {
			isWorking.set(false);
			if(data.silentFail) {
				Log.e(TAG,"Could not create blur view",t);
			} else {
				throw new RuntimeException("Error while updating the live blur",t);
			}
		}
		return false;
	}

	/**
	 * Draws the given view to a canvas with the given scale (higher = smaller)
	 * @param dest
	 * @param view
	 * @param downSampling
	 * @return
	 */
	private static Bitmap drawViewToBitmap(Bitmap dest, View view, int downSampling, Bitmap.Config bitmapConfig) {
		float scale = 1f / downSampling;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int bmpWidth = Math.round(viewWidth * scale);
		int bmpHeight = Math.round(viewHeight * scale);

		if (dest == null || dest.getWidth() != bmpWidth || dest.getHeight() != bmpHeight) {
			dest = Bitmap.createBitmap(bmpWidth, bmpHeight, bitmapConfig);
		}

		Canvas c = new Canvas(dest);
		if (downSampling > 1) {
			c.scale(scale, scale);
		}

		view.draw(c);
		return dest;
	}



	/**
	 * crops the srcBmp with the canvasView bounds and returns the cropped bitmap
	 */
	private static Bitmap crop(Bitmap srcBmp, View canvasView, int downsampling) {
		float scale = 1f / downsampling;
		return Bitmap.createBitmap(
				srcBmp,
				(int) Math.floor((LegacySDKUtil.getX(canvasView))*scale),
				(int) Math.floor((LegacySDKUtil.getY(canvasView))*scale),
				(int) Math.floor((canvasView.getWidth())*scale),
				(int) Math.floor((canvasView.getHeight())*scale)
		);
	}




}
