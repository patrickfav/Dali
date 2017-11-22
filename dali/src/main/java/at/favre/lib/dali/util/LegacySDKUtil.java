package at.favre.lib.dali.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

public final class LegacySDKUtil {

    private LegacySDKUtil() {
    }

    public static float getX(View v) {
        if (Build.VERSION.SDK_INT >= 11) {
            return v.getX();
        } else {
            return v.getLeft();
        }
    }

    public static float getY(View v) {
        if (Build.VERSION.SDK_INT >= 11) {
            return v.getY();
        } else {
            return v.getTop();
        }
    }

    /**
     * legacy helper for setting background
     */
    public static void setViewBackground(View v, Drawable d) {
        if (Build.VERSION.SDK_INT >= 16) {
            v.setBackground(d);
        } else {
            v.setBackgroundDrawable(d);
        }
    }

    /**
     * returns the bytesize of the give bitmap
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static String getBitmapId(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return String.valueOf(bitmap.getGenerationId());
        } else {
            //TODO: this is really since bitmap has no implementation of hashcode
            return String.valueOf(bitmap.hashCode());
        }
    }

    /**
     * Gets the appropriate cache dir
     *
     * @param ctx
     * @return
     */
    public static String getCacheDir(Context ctx) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || (!Environment.isExternalStorageRemovable() && ctx.getExternalCacheDir() != null) ?
                ctx.getExternalCacheDir().getPath() : ctx.getCacheDir().getPath();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setImageAlpha(ImageView imageView, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            imageView.setImageAlpha(alpha);
        } else {
            imageView.setAlpha(alpha);
        }
    }
}
