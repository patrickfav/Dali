package at.favre.lib.dali.blur.algorithms;

import android.graphics.Bitmap;

import at.favre.lib.dali.blur.IBlur;

/**
 * This is the default algorithm, that does nothing but returns
 * the original bitmap
 */
public class IgnoreBlur implements IBlur {
    @Override
    public Bitmap blur(int radius, Bitmap original) {
        return original;
    }
}
