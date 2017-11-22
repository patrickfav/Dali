package at.favre.lib.dali.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * This is a simple extension to the scrollView that
 * allows to set a scrollListener
 */
public class ObservableScrollView extends ScrollView {
    private Observers.ScrollViewListener scrollViewListener = null;
    private Observers.DrawListener onDrawListener;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(Observers.ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
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

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

}
