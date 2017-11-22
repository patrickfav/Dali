package at.favre.lib.dali.builder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * This is a wrapper for the different
 * options one can specify a reference of
 * an image. This will only load (if necessary)
 * the image ondemand.
 */
public class ImageReference {
    private static final String CACHE_KEY_PREFIX = "cachekey_";

    public enum SourceType {
        RES_ID, INPUT_STREAM, BITMAP, FILE, VIEW, UNKNOWN
    }

    private Integer resId;
    private InputStream inputStream;
    private Bitmap bitmap;
    private File fileToBitmap;
    private View view;

    private String contentId;
    private SourceType type = SourceType.UNKNOWN;

    private BitmapFactory.Options decoderOptions;

    public ImageReference(int resId) {
        this.resId = resId;
        this.contentId = "resid_" + resId;
        this.type = SourceType.RES_ID;
    }

    public ImageReference(InputStream inputStream) {
        this(inputStream, String.valueOf(inputStream.hashCode()));
    }

    public ImageReference(InputStream inputStream, String cacheKey) {
        this.inputStream = inputStream;
        this.contentId = CACHE_KEY_PREFIX + cacheKey;
        this.type = SourceType.INPUT_STREAM;
    }

    public ImageReference(Bitmap bitmap) {
        this(bitmap, LegacySDKUtil.getBitmapId(bitmap));
    }

    public ImageReference(Bitmap bitmap, String cacheKey) {
        this.bitmap = bitmap;
        this.contentId = CACHE_KEY_PREFIX + cacheKey;
        this.type = SourceType.BITMAP;
    }

    public ImageReference(File fileToBitmap) {
        this(fileToBitmap, "file_" + fileToBitmap.getAbsolutePath());
    }

    public ImageReference(File fileToBitmap, String cacheKey) {
        this.fileToBitmap = fileToBitmap;
        this.contentId = cacheKey;
        this.type = SourceType.FILE;
    }

    public ImageReference(View view) {
        this.view = view;
        this.contentId = "view_" + view.hashCode();
        this.type = SourceType.VIEW;
    }

    public void setDecoderOptions(BitmapFactory.Options decoderOptions) {
        this.decoderOptions = decoderOptions;
    }

    public BitmapFactory.Options getDecoderOptions() {
        return decoderOptions;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    /**
     * This returns a string which identifies the content. Take care to
     * give your own key in inputStream and bitmap since these usually have
     * weak ids.
     * <p>
     * This is used for caching as a key.
     * <p>
     * There is no restriction what kind of chars this can return, so only use
     * this either hashed or base64 encoded or similar if using in a filesystem.
     *
     * @return the id or the empty string if the id could not be generated
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * This will synchronously load the bitmap (if needed) maybe don't
     * call this in the main thread. Loading of large bitmaps can take up
     * 500 ms+ on faster devices.
     *
     * @param resources {@link android.content.Context#getResources()}
     * @return the loaded bitmap. If custom options are provided these will be used here
     */
    public Bitmap synchronouslyLoadBitmap(Resources resources) {
        if (bitmap != null) {
            if (decoderOptions != null && decoderOptions.inSampleSize > 1) {
                return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / decoderOptions.inSampleSize, bitmap.getHeight() / decoderOptions.inSampleSize, false);
            } else {
                return bitmap;
            }
        } else if (resId != null) {
            return BitmapFactory.decodeResource(resources, resId, decoderOptions);
        } else if (fileToBitmap != null) {
            return BitmapFactory.decodeFile(fileToBitmap.getAbsolutePath(), decoderOptions);
        } else if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream, null, decoderOptions);
        } else if (view != null) {
            int downSample = 1;
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            if (decoderOptions != null) {
                if (decoderOptions.inSampleSize > 1) {
                    downSample = decoderOptions.inSampleSize;
                }
                if (decoderOptions.inPreferredConfig != null) {
                    config = decoderOptions.inPreferredConfig;
                }
            }
            return BuilderUtil.drawViewToBitmap(bitmap, view, downSample, config);
        }
        throw new IllegalStateException("No image resource was set");
    }

    /**
     * If the not a bitmap itself, this will read the file's meta data.
     *
     * @param resources {@link android.content.Context#getResources()}
     * @return Point where x = width and y = height
     */
    public Point measureImage(Resources resources) {
        BitmapFactory.Options justBoundsOptions = new BitmapFactory.Options();
        justBoundsOptions.inJustDecodeBounds = true;

        if (bitmap != null) {
            return new Point(bitmap.getWidth(), bitmap.getHeight());
        } else if (resId != null) {
            BitmapFactory.decodeResource(resources, resId, justBoundsOptions);
            float scale = (float) justBoundsOptions.inTargetDensity / justBoundsOptions.inDensity;
            return new Point((int) (justBoundsOptions.outWidth * scale + 0.5f), (int) (justBoundsOptions.outHeight * scale + 0.5f));
        } else if (fileToBitmap != null) {
            BitmapFactory.decodeFile(fileToBitmap.getAbsolutePath(), justBoundsOptions);
        } else if (inputStream != null) {
            BitmapFactory.decodeStream(inputStream, null, justBoundsOptions);
            try {
                inputStream.reset();
            } catch (IOException ignored) {
            }
        } else if (view != null) {
            return new Point(view.getWidth(), view.getHeight());
        }
        return new Point(justBoundsOptions.outWidth, justBoundsOptions.outHeight);
    }

    /**
     * Returns the readable type of source data (eg. view, bitmap, file, etc.)
     */
    public SourceType getSourceType() {
        return type;
    }

    public Integer getResId() {
        return resId;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public File getFileToBitmap() {
        return fileToBitmap;
    }

    public View getView() {
        return view;
    }
}
