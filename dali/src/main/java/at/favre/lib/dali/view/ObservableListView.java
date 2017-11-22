package at.favre.lib.dali.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class ObservableListView extends ListView {

    private Observers.DrawListener onDrawListener;

    public ObservableListView(Context context) {
        super(context);
    }

    public ObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
