package at.favre.lib.dali.blur.algorithms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import at.favre.lib.dali.blur.IBlur;

/**
 * http://stackoverflow.com/questions/8218438
 * by saarraz1
 */
public class BoxBlur implements IBlur {
    @Override
    public Bitmap blur(int radius, Bitmap bmp) {
        if ((radius & 1) == 0) throw new IllegalArgumentException("Range must be odd.");

        Bitmap blurred = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(blurred);

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        boxBlurHorizontal(pixels, w, h, radius / 2);
        boxBlurVertical(pixels, w, h, radius / 2);

        c.drawBitmap(pixels, 0, w, 0.0F, 0.0F, w, h, true, null);

        return blurred;
    }

    private static void boxBlurHorizontal(int[] pixels, int w, int h,
                                          int halfRange) {
        int index = 0;
        int[] newColors = new int[w];

        for (int y = 0; y < h; y++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;
            for (int x = -halfRange; x < w; x++) {
                int oldPixel = x - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixel];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }

                int newPixel = x + halfRange;
                if (newPixel < w) {
                    int color = pixels[index + newPixel];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }

                if (x >= 0) {
                    newColors[x] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }
            }

            System.arraycopy(newColors, 0, pixels, index + 0, w);

            index += w;
        }
    }

    private static void boxBlurVertical(int[] pixels, int w, int h,
                                        int halfRange) {

        int[] newColors = new int[h];
        int oldPixelOffset = -(halfRange + 1) * w;
        int newPixelOffset = (halfRange) * w;

        for (int x = 0; x < w; x++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;
            int index = -halfRange * w + x;
            for (int y = -halfRange; y < h; y++) {
                int oldPixel = y - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixelOffset];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }

                int newPixel = y + halfRange;
                if (newPixel < h) {
                    int color = pixels[index + newPixelOffset];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }

                if (y >= 0) {
                    newColors[y] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }

                index += w;
            }

            for (int y = 0; y < h; y++) {
                pixels[y * w + x] = newColors[y];
            }
        }
    }
}
