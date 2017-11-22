package at.favre.lib.dali.builder.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlurKeyFrameTransitionAnimation {
    private static final String TAG = BlurKeyFrameTransitionAnimation.class.getSimpleName();

    private List<TransitionDrawable> transitionDrawables = new ArrayList<TransitionDrawable>();
    private Context ctx;
    private Handler handler = new Handler(Looper.getMainLooper());
    private BlurKeyFrameManager manager;

    private List<Runnable> runnables;

    private boolean running = false;
    private boolean canceled = false;
    private KeyFrameAnimationListener listener;

    public BlurKeyFrameTransitionAnimation(Context ctx, BlurKeyFrameManager manager) {
        this.ctx = ctx;
        this.manager = manager;
        this.runnables = new CopyOnWriteArrayList<Runnable>();
    }

    public void prepareAnimation(Bitmap original) {
        BlurKeyFrameManager.KeyFrameData data = manager.prepareFrames(ctx, original);
        transitionDrawables = new ArrayList<TransitionDrawable>();

        for (int i = 0; i < data.getFrames().size(); i++) {
            if (i + 1 < data.getFrames().size()) {
                TransitionDrawable t = new TransitionDrawable(new Drawable[]{new BitmapDrawable(ctx.getResources(), data.getFrames().get(i)), new BitmapDrawable(ctx.getResources(), data.getFrames().get(i + 1))});
                transitionDrawables.add(t);
            }
        }
    }

    public void start(final ImageView imageView) {
        if (listener != null) {
            listener.onAnimationStart();
        }
        long duration = 0;
        for (int i = 0; i < transitionDrawables.size(); i++) {
            final int iterator = i;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionDrawable t = transitionDrawables.get(iterator);
                    t.startTransition(manager.getKeyFrames().get(iterator).getDuration());
                    imageView.setImageDrawable(t);
                    Log.d(TAG, "transition " + iterator);
                }
            }, duration);
            duration += manager.getKeyFrames().get(i).getDuration();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "reset animation");
                for (TransitionDrawable transitionDrawable : transitionDrawables) {
                    transitionDrawable.resetTransition();
                }

                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }
        }, duration);
    }

    public synchronized void cancel() {
        this.canceled = true;
        this.running = false;
    }

    public interface KeyFrameAnimationListener {
        void onAnimationStart();

        void onKeyFrameChange(int keyFrameNo);

        void onAnimationEnd();
    }

}
