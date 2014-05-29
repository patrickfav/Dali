package at.favre.lib.dali.builder.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class BlurAnimation  {
	public List<TransitionDrawable> transitionDrawables = new ArrayList<TransitionDrawable>();
	public Context ctx;
	public Handler handler = new Handler(Looper.getMainLooper());
	public BlurKeyFrameManager manager;

	public BlurAnimation(Context ctx,BlurKeyFrameManager manager) {
		this.ctx = ctx;
		this.manager = manager;
	}

	public void prepareAnimation(Bitmap original) {
		BlurKeyFrameManager.KeyFrameData data = manager.prepareFrames(ctx,original);

		for (int i = 0; i < data.getFrames().size(); i++) {
			if(i+1 < data.getFrames().size()) {
				TransitionDrawable t = new TransitionDrawable(new Drawable[]{new BitmapDrawable(ctx.getResources(),data.getFrames().get(i)),new BitmapDrawable(ctx.getResources(),data.getFrames().get(i+1))});
				t.setCrossFadeEnabled(false);
				transitionDrawables.add(t);
			}
		}
	}

	public void start(final ImageView imageView) {
		long duration = 0;
		for (int i = 0; i < transitionDrawables.size(); i++) {
			final int iterator = i;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					transitionDrawables.get(iterator).startTransition(manager.getKeyFrames().get(iterator).getDuration());
					imageView.setImageDrawable(transitionDrawables.get(iterator));
				}
			},duration);
			duration += manager.getKeyFrames().get(i).getDuration();
		}
	}



}
