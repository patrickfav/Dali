package at.favre.lib.dali.builder;

import android.view.View;

import java.util.List;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.blur.IBlur;
import at.favre.lib.dali.util.BlurUtil;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class LiveBlurBuilder {
	private final static String TAG = BlurBuilder.class.getSimpleName();

	private Data data;
	protected class Data {
		public boolean debugMode=false;
		public View rootView;
		public List<View> viewsToBlurOnto;
		public int inSampleSize = 8;
		public int blurRadius = 16;
		public IBlur blurAlgorithm;

		public ContextWrapper contextWrapper;
	}

	public LiveBlurBuilder(ContextWrapper contextWrapper, View rootView, List<View> viewsToBlurOnto, boolean debugMode) {
		data=new Data();

		data.rootView = rootView;
		data.viewsToBlurOnto = viewsToBlurOnto;
		data.contextWrapper = contextWrapper;
		data.blurAlgorithm = BlurUtil.getIBlurAlgorithm(EBlurAlgorithm.RS_GAUSS_FAST,contextWrapper);
		data.debugMode = debugMode;
	}

	public LiveBlurBuilder downSample(int inSampleSize) {
		data.inSampleSize = inSampleSize;
		return this;
	}

	public LiveBlurWorker assemble() {
		return new LiveBlurWorker(data);
	}

}
