package at.favre.lib.dali.builder;

import at.favre.lib.dali.blur.IBlur;

/**
 * Created by PatrickF on 29.05.2014.
 */
public abstract class ABuilder {

    public static class Data {
        public int blurRadius = BuilderDefaults.BLUR_RADIUS;
        public IBlur blurAlgorithm;

        public ContextWrapper contextWrapper;
    }
}
