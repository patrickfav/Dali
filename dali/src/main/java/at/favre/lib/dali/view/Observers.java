package at.favre.lib.dali.view;

import android.graphics.Canvas;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class Observers {
	public interface DrawListener {
		public void onDraw(Canvas canvas);
	}
	public interface ScrollViewListener {
		void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
	}
}
