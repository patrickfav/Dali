package at.favre.lib.dali.builder.blur;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.PerformanceProfiler;
import at.favre.lib.dali.builder.exception.BlurWorkerException;
import at.favre.lib.dali.builder.processor.IBitmapProcessor;
import at.favre.lib.dali.util.BenchmarkUtil;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * This is the worker thread for a the {@link at.favre.lib.dali.builder.blur.BlurBuilder}. It
 * contains all the business logic for processing the image.
 */
public class BlurWorker implements Callable<BlurWorker.Result> {
    private static final String TAG = BlurWorker.class.getSimpleName();

    private final String id = UUID.randomUUID().toString();
    private BlurWorkerListener listener;
    private BlurBuilder.BlurData builderData;
    private final Semaphore semaphore = new Semaphore(0, true);
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public BlurWorker(BlurBuilder.BlurData builderData) {
        this(builderData, null);
    }

    public BlurWorker(BlurBuilder.BlurData builderData, BlurWorkerListener listener) {
        this.builderData = builderData;
        this.listener = listener;
    }

    @Override
    public Result call() {
        try {
            Result r = new Result(process());
            if (listener != null) {
                listener.onResult(r);
            }
            return r;
        } catch (Throwable t) {
            Result r = new Result(t);
            if (listener != null) {
                listener.onResult(r);
            }
            return r;
        }
    }

    /**
     * The core process
     */
    private Bitmap process() {
        PerformanceProfiler profiler = new PerformanceProfiler("blur image task [" + builderData.tag + "] started at " + BenchmarkUtil.getCurrentTime(), Dali.getConfig().debugMode);
        try {

            final String cacheKey = BuilderUtil.getCacheKey(builderData);

            if (builderData.shouldCache) {
                profiler.startTask(-3, "cache lookup (key:" + cacheKey + ")");
                Bitmap cache = builderData.diskCacheManager.get(cacheKey);
                profiler.endTask(-3, cache == null ? "miss" : "hit");
                if (cache != null) {
                    profiler.printResultToLog();
                    return cache;
                }
            } else {
                builderData.diskCacheManager.purge(cacheKey);
            }

            if (builderData.imageReference.getSourceType().equals(ImageReference.SourceType.VIEW)) {
                profiler.startTask(-2, "wait for view to be measured");
                View v = builderData.imageReference.getView();
                uiThreadHandler.post(new WaitForMeasurement(semaphore, v));

                Dali.logV(TAG, "aquire lock for waiting for the view to be measured");
                if (semaphore.tryAcquire(8000, TimeUnit.MILLISECONDS)) {
                    Dali.logV(TAG, "view seems measured, lock was released");
                } else {
                    throw new InterruptedException("Timeout while waiting for the view to be measured");
                }
                profiler.endTask(-2);
            }

//            Thread.sleep(1000);

            int width = 0, height = 0;
            if (builderData.options.inSampleSize > 1 && builderData.rescaleIfDownscaled) {
                profiler.startTask(-1, "measure image");
                Point p = builderData.imageReference.measureImage(builderData.contextWrapper.getResources());
                width = p.x;
                height = p.y;
                profiler.endTask(-1, height + "x" + width);
            }

            profiler.startTask(0, "load image");
            builderData.imageReference.setDecoderOptions(builderData.options);
            Bitmap bitmapToWorkWith = builderData.imageReference.synchronouslyLoadBitmap(builderData.contextWrapper.getResources());
            profiler.endTask(0, "source: " + builderData.imageReference.getSourceType() + ", insample: "
                    + builderData.options.inSampleSize + ", height:" + bitmapToWorkWith.getHeight() + ", width:" + bitmapToWorkWith.getWidth() +
                    ", memory usage " + BenchmarkUtil.getScalingUnitByteSize(LegacySDKUtil.byteSizeOf(bitmapToWorkWith)));

            if (builderData.copyBitmapBeforeBlur) {
                profiler.startTask(1, "copy bitmap");
                bitmapToWorkWith = bitmapToWorkWith.copy(bitmapToWorkWith.getConfig(), true);
                profiler.endTask(1);
            }

            int profileIdPreProcessor = 100;
            for (IBitmapProcessor postProcessor : builderData.preProcessors) {
                profiler.startTask(profileIdPreProcessor, postProcessor.getProcessorTag());
                bitmapToWorkWith = postProcessor.manipulate(bitmapToWorkWith);
                profiler.endTask(profileIdPreProcessor++);
            }

            profiler.startTask(10000, "blur with radius " + builderData.blurRadius + "px (" + builderData.blurRadius * builderData.options.inSampleSize + "spx) and algorithm " + builderData.blurAlgorithm.getClass().getSimpleName());
            bitmapToWorkWith = builderData.blurAlgorithm.blur(builderData.blurRadius, bitmapToWorkWith);
            profiler.endTask(10000);

            int profileIdPostProcessor = 20000;
            for (IBitmapProcessor postProcessor : builderData.postProcessors) {
                profiler.startTask(profileIdPostProcessor, postProcessor.getProcessorTag());
                bitmapToWorkWith = postProcessor.manipulate(bitmapToWorkWith);
                profiler.endTask(profileIdPostProcessor++);
            }

            if (builderData.options.inSampleSize > 1 && builderData.rescaleIfDownscaled && height > 0 && width > 0) {
                profiler.startTask(40000, "rescale to " + height + "x" + width);
                bitmapToWorkWith = Bitmap.createScaledBitmap(bitmapToWorkWith, width, height, false);
                profiler.endTask(40000, "memory usage " + BenchmarkUtil.getScalingUnitByteSize(LegacySDKUtil.byteSizeOf(bitmapToWorkWith)));
            }

            if (builderData.shouldCache) {
                profiler.startTask(40001, "async try to disk cache (ignore result)");
                Dali.getExecutorManager().executeOnFireAndForgetThreadPool(new AddToCacheTask(bitmapToWorkWith, builderData, cacheKey));
                profiler.endTask(40001);
            }

            return bitmapToWorkWith;
        } catch (Throwable t) {
            throw new BlurWorkerException(t);
        } finally {
            profiler.printResultToLog();
        }
    }

    public String getId() {
        return id;
    }

    public static class WaitForMeasurement implements Runnable {
        private Semaphore semaphore;
        private View v;

        public WaitForMeasurement(Semaphore semaphore, View v) {
            this.semaphore = semaphore;
            this.v = v;
        }

        @Override
        public void run() {
            v.post(new Runnable() {
                @Override
                public void run() {
                    Dali.logV(TAG, "in view message queue, seems measured, will unlock");
                    semaphore.release();
                }
            });
        }
    }

    public static class AddToCacheTask implements Runnable {
        private Bitmap bitmap;
        private BlurBuilder.BlurData data;
        private String cacheKey;

        public AddToCacheTask(Bitmap bitmap, BlurBuilder.BlurData data, String cacheKey) {
            this.bitmap = bitmap;
            this.data = data;
            this.cacheKey = cacheKey;
        }

        @Override
        public void run() {
            data.diskCacheManager.putInCache(bitmap, cacheKey);
        }
    }

    public static class Result {
        private Bitmap bitmap;
        private Throwable throwable;

        public Result(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Result(Throwable t) {
            this.throwable = t;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public boolean isError() {
            return throwable != null;
        }
    }

    public interface BlurWorkerListener {
        void onResult(Result result);
    }

}
