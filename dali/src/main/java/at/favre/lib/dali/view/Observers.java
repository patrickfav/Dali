package at.favre.lib.dali.view;

import android.graphics.Canvas;

public class Observers {
    public interface DrawListener {
        void onDraw(Canvas canvas);
    }

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}
