package at.favre.lib.dali.builder;

import at.favre.lib.dali.blur.IBlur;

/**
 * Created by PatrickF on 29.05.2014.
 */
public abstract class ABuilder {

	protected static class Data {
		public boolean debugMode=false;
		public int blurRadius = BuilderDefaults.BLUR_RADIUS;
		public IBlur blurAlgorithm;

		public ContextWrapper contextWrapper;
	}
}
