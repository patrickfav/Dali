package at.favre.lib.dali.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public final class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    private BitmapUtil() {
    }

    public static void clearCacheDir(File cacheDir) {
        File[] files = cacheDir.listFiles();

        if (files != null) {
            for (File file : files)
                file.delete();
        }
    }

    public static Bitmap loadBitmapFromResId(int resId, Resources resources) {
        return BitmapFactory.decodeResource(resources, resId);
    }

    public static File saveBitmapDownscaled(Bitmap bitmap, String filename, String path, boolean recycle, int maxWidth, int maxHeight) {
        float heightScaleFactor = 1;
        float widthScaleFactor = 1;
        float scaleFactor = 1;

        if (bitmap.getHeight() > maxHeight) {
            heightScaleFactor = (float) maxHeight / (float) bitmap.getHeight();
        }

        if (bitmap.getWidth() > maxWidth) {
            widthScaleFactor = (float) maxWidth / (float) bitmap.getWidth();
        }
        if (heightScaleFactor < 1 || widthScaleFactor < 1) {
            scaleFactor = Math.min(heightScaleFactor, widthScaleFactor);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scaleFactor), (int) (bitmap.getHeight() * scaleFactor), true);
        return saveBitmap(bitmap, filename, path, recycle);
    }

    public static File saveBitmap(Bitmap bitmap, String filename, String path, boolean recycle) {
        FileOutputStream out = null;
        try {
            File f = new File(path, filename);
            if (!f.exists()) {
                f.createNewFile();
            }
            out = new FileOutputStream(f);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                return f;
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not save bitmap", e);
        } finally {
            try {
                out.close();
            } catch (Throwable ignore) {
            }
            if (recycle) {
                bitmap.recycle();
            }
        }
        return null;
    }

    /**
     * Mirrors the given bitmap
     */

    public static Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
    }

    /**
     * @param bmp        input bitmap
     * @param contrast   0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static int getInSampleSizeFromScale(float scale, boolean keepPowOfTwo) {
        int insample = 1;
        float scaleThreshold = 1.f;

        while (scaleThreshold >= scale) {
            if (keepPowOfTwo) {
                insample *= 2;
            } else {
                insample += 1;
            }

            scaleThreshold = scaleThreshold / insample;
        }

        if (keepPowOfTwo) {
            return insample / 2;
        } else {
            return insample - 1;
        }
    }
}
