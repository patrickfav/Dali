package at.favre.lib.dali.builder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

import at.favre.lib.dali.util.LogUtil;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class LiveBlurWorker {
	private final static String TAG = LiveBlurWorker.class.getSimpleName();

	private AtomicBoolean isWorking = new AtomicBoolean(false);
	private Bitmap dest;

	private LiveBlurBuilder.Data data;

	public LiveBlurWorker(LiveBlurBuilder.Data data) {
		this.data = data;
	}

	public boolean updateBlurView() {
		try {
			if(data.rootView == null || data.viewsToBlurOnto.isEmpty()) {
				LogUtil.logDebug(TAG,"Views not set",data.debugMode);
				return false;
			}

			if( data.viewsToBlurOnto.get(0).getWidth() == 0 || data.viewsToBlurOnto.get(0).getHeight() == 0) {
				LogUtil.logDebug(TAG,"Views not ready to be blurred",data.debugMode);
				return false;
			}

			if (!isWorking.get()) {
				isWorking.compareAndSet(false, true);

				dest = drawViewToBitmap(dest, data.rootView, data.inSampleSize);

				for (View view : data.viewsToBlurOnto) {
					Drawable d = new BitmapDrawable(data.contextWrapper.getResources(), data.blurAlgorithm.blur(data.blurRadius,crop(dest.copy(dest.getConfig(), true), view, data.inSampleSize)));
					setViewBackground(view, d);
				}
				isWorking.compareAndSet(true, false);
				return true;
			} else {
				LogUtil.logDebug(TAG,"Skip blur frame, already in blur",data.debugMode);
			}
		} catch (Throwable t) {
			Log.e(TAG,"Could not create blur view",t);
		}
		return false;
	}

	private Bitmap drawViewToBitmap(Bitmap dest, View view, int downSampling) {
		float scale = 1f / downSampling;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int bmpWidth = Math.round(viewWidth * scale);
		int bmpHeight = Math.round(viewHeight * scale);

		if (dest == null || dest.getWidth() != bmpWidth || dest.getHeight() != bmpHeight) {
			dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
		}

		Canvas c = new Canvas(dest);
		if (downSampling > 1) {
			c.scale(scale, scale);
		}

		view.draw(c);
		return dest;
	}

	private void setViewBackground(View v, Drawable d) {
		if (Build.VERSION.SDK_INT >= 16) {
			v.setBackground(d);
		} else {
			v.setBackgroundDrawable(d);
		}
	}

	private Bitmap crop(Bitmap srcBmp, View canvasView, int downsampling) {
		float scale = 1f / downsampling;
		return Bitmap.createBitmap(
				srcBmp,
				(int) Math.floor((canvasView.getX())*scale),
				(int) Math.floor((canvasView.getY())*scale),
				(int) Math.floor((canvasView.getWidth())*scale),
				(int) Math.floor((canvasView.getHeight())*scale)
		);
	}
}
