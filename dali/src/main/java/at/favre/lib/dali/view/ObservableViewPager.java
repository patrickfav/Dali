package at.favre.lib.dali.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class ObservableViewPager extends ViewPager {

    private Observers.DrawListener onDrawListener;

    public ObservableViewPager(Context context) {
        super(context);
    }

    public ObservableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onDrawListener != null) {
            onDrawListener.onDraw(canvas);
        }
    }

    public void setOnDrawListener(Observers.DrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }
}
