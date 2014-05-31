package at.favre.lib.dali.builder.blur;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.concurrent.Callable;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.PerformanceProfiler;
import at.favre.lib.dali.builder.processor.IBitmapProcessor;
import at.favre.lib.dali.util.BenchmarkUtil;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class BlurWorker implements Callable<BlurWorker.Result> {
	private final static String TAG = BlurWorker.class.getSimpleName();

	private BlurBuilder.BlurData builderData;

	public BlurWorker(BlurBuilder.BlurData builderData) {
		this.builderData = builderData;
	}

	@Override
	public Result call() {
		try {
			return new Result(process());
		} catch (Throwable t) {
			return new Result(t);
		}
	}

	public Bitmap process() {
		PerformanceProfiler profiler = new PerformanceProfiler("blur image", Dali.getConfig().debugMode);

		final String cacheKey = BuilderUtil.getCacheKey(builderData);

		if(builderData.shouldCache) {
			profiler.startTask(-2, "cache lookup (key:" + cacheKey + ")");
			Bitmap cache = builderData.diskCacheManager.get(cacheKey);
			profiler.endTask(-2,cache == null ? "miss":"hit");
			if(cache != null) {
				profiler.printResultToLog();
				return cache;
			}
		}

		int width=0,height=0;
		if(builderData.options.inSampleSize > 1 && builderData.rescaleIfDownscaled) {
			profiler.startTask(-1, "measure image");
			Point p = builderData.imageReference.measureImage(builderData.contextWrapper.getResources());
			width = p.x;
			height = p.y;
			profiler.endTask(-1, height+"x"+width);
		}

		profiler.startTask(0, "load bitmap");
		builderData.imageReference.setDecoderOptions(builderData.options);
		Bitmap bitmapToWorkWith = builderData.imageReference.synchronouslyLoadBitmap(builderData.contextWrapper.getResources());
		profiler.endTask(0, "insample: "+builderData.options.inSampleSize+", height:" + bitmapToWorkWith.getHeight() + ", width:" + bitmapToWorkWith.getWidth() + ", memory usage " + BenchmarkUtil.getScalingUnitByteSize(LegacySDKUtil.byteSizeOf(bitmapToWorkWith)));

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

		profiler.startTask(10000, "blur with radius " + builderData.blurRadius + " and algorithm " + builderData.blurAlgorithm.getClass().getSimpleName());
		bitmapToWorkWith = builderData.blurAlgorithm.blur(builderData.blurRadius,bitmapToWorkWith);
		profiler.endTask(10000);

		int profileIdPostProcessor = 20000;
		for (IBitmapProcessor postProcessor : builderData.postProcessors) {
			profiler.startTask(profileIdPostProcessor, postProcessor.getProcessorTag());
			bitmapToWorkWith = postProcessor.manipulate(bitmapToWorkWith);
			profiler.endTask(profileIdPostProcessor++);
		}

		if(builderData.options.inSampleSize > 1 && builderData.rescaleIfDownscaled) {
			profiler.startTask(40000, "rescale to "+height +"x"+ width);
			bitmapToWorkWith = Bitmap.createScaledBitmap(bitmapToWorkWith, width, height, false);
			profiler.endTask(40000);
		}

		if(builderData.shouldCache) {
			profiler.startTask(40001, "async try to disk cache");
			Dali.getExecutorManager().executeOnCacheThreadPool(new AddToCacheTask(bitmapToWorkWith, builderData, cacheKey));
			profiler.endTask(40001);
		}

		profiler.printResultToLog();


		return bitmapToWorkWith;
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

}
