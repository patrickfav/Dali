package at.favre.lib.dali.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.renderscript.RenderScript;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.blur.IBlur;
import at.favre.lib.dali.blur.algorithms.BoxBlur;
import at.favre.lib.dali.blur.algorithms.GaussianFastBlur;
import at.favre.lib.dali.blur.algorithms.IgnoreBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptBox5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussian5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussianBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptStackBlur;
import at.favre.lib.dali.blur.algorithms.StackBlur;
import at.favre.lib.dali.builder.BuilderDefaults;
import at.favre.lib.dali.builder.ContextWrapper;
import at.favre.lib.dali.builder.blur.BlurBuilder;
import at.favre.lib.dali.builder.processor.IBitmapProcessor;

public final class BuilderUtil {

    private BuilderUtil() {
    }

    /**
     * Creates an IBlur instance for the given algorithm enum
     *
     * @param algorithm
     * @param contextWrapper
     * @return
     */
    public static IBlur getIBlurAlgorithm(EBlurAlgorithm algorithm, ContextWrapper contextWrapper) {
        RenderScript rs = contextWrapper.getRenderScript();
        Context ctx = contextWrapper.getContext();

        switch (algorithm) {
            case RS_GAUSS_FAST:
                return new RenderScriptGaussianBlur(rs);
            case RS_BOX_5x5:
                return new RenderScriptBox5x5Blur(rs);
            case RS_GAUSS_5x5:
                return new RenderScriptGaussian5x5Blur(rs);
            case RS_STACKBLUR:
                return new RenderScriptStackBlur(rs, ctx);
            case STACKBLUR:
                return new StackBlur();
            case GAUSS_FAST:
                return new GaussianFastBlur();
            case BOX_BLUR:
                return new BoxBlur();
            default:
                return new IgnoreBlur();
        }
    }

    /**
     * Check if blur radius is within valid range of {@link at.favre.lib.dali.builder.BuilderDefaults#BLUR_RADIUS_MIN} and {@link at.favre.lib.dali.builder.BuilderDefaults#BLUR_RADIUS_MAX}
     *
     * @param blurRadius
     */
    public static void checkBlurRadiusPrecondition(int blurRadius) {
        if (blurRadius < BuilderDefaults.BLUR_RADIUS_MIN || blurRadius > BuilderDefaults.BLUR_RADIUS_MAX) {
            throw new IllegalArgumentException("Valid blur radius must be between (inclusive) " + BuilderDefaults.BLUR_RADIUS_MIN + " and " + BuilderDefaults.BLUR_RADIUS_MAX + " found " + blurRadius);
        }
    }

    public static String getBuilderDescription(BlurBuilder.BlurData data) {
        StringBuilder sb = new StringBuilder();

        sb.append(data.imageReference.getContentId() + ", ");
        sb.append("radius: " + data.blurRadius + ", ");
        sb.append(data.blurAlgorithm.getClass().getSimpleName() + ", ");
        sb.append("rescaleIfDownScale: " + data.rescaleIfDownscaled + ", ");

        for (IBitmapProcessor preProcessor : data.preProcessors) {
            sb.append(preProcessor.getProcessorTag() + ", ");
        }
        for (IBitmapProcessor postProcessor : data.postProcessors) {
            sb.append(postProcessor.getProcessorTag() + ", ");
        }

        if (data.options != null) {
            sb.append("sampleSize: " + data.options.inSampleSize + ", ");
        }

        return sb.toString();
    }

    public static String getCacheKey(BlurBuilder.BlurData data) {
        return sha1Hash(getBuilderDescription(data));
    }

    public static String sha1Hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes(StandardCharsets.ISO_8859_1), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash", e);
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Draws the given view to a canvas with the given scale (higher = smaller)
     *
     * @param dest
     * @param view
     * @param downSampling
     * @return
     */
    public static Bitmap drawViewToBitmap(Bitmap dest, View view, int downSampling, Bitmap.Config bitmapConfig) {
        float scale = 1f / downSampling;
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        int bmpWidth = Math.round(viewWidth * scale);
        int bmpHeight = Math.round(viewHeight * scale);

        if (dest == null || dest.getWidth() != bmpWidth || dest.getHeight() != bmpHeight) {
            dest = Bitmap.createBitmap(bmpWidth, bmpHeight, bitmapConfig);
        }

        Canvas c = new Canvas(dest);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }

        view.draw(c);
        return dest;
    }

    public static void logDebug(String tag, String msg, boolean shouldLog) {
        if (shouldLog) {
            Log.d(tag, msg);
        }
    }

    public static void logVerbose(String tag, String msg, boolean shouldLog) {
        if (shouldLog) {
            Log.v(tag, msg);
        }
    }

    public static void checkMustNotRunOnUiThread() {
        if (isOnUiThread()) {
            throw new IllegalStateException("This method must NOT be called from the ui thread which is " + Looper.getMainLooper() + " was called from " + Looper.myLooper() + ".");
        }
    }

    public static boolean isOnUiThread() {
        return Looper.myLooper() == null || Looper.myLooper() == Looper.getMainLooper();
    }
}
