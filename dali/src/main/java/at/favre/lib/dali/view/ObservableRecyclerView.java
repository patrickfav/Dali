package at.favre.lib.dali.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ObservableRecyclerView extends RecyclerView {
    private Observers.DrawListener onDrawListener;

    public ObservableRecyclerView(Context context) {
        super(context);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onDrawListener != null) {
            onDrawListener.onDraw(canvas);
        }
    }

    public void setOnDrawListener(Observers.DrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }
}
