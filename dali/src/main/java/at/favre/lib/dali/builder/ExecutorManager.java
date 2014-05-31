package at.favre.lib.dali.builder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import at.favre.lib.dali.builder.blur.BlurWorker;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class ExecutorManager {
	private static final int MAX_QUEUE = 15;

	private ExecutorService mainThreadPool;
	private ExecutorService fireAndForgetThreadPool;


	public ExecutorManager(int maxConcurrentMainWorkers) {
		mainThreadPool = new ThreadPoolExecutor(1, maxConcurrentMainWorkers,
				2500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));

		fireAndForgetThreadPool = new ThreadPoolExecutor(2, 4,500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));
	}

	public Future<BlurWorker.Result> submitThreadPool(Callable<BlurWorker.Result> callable) {
		return mainThreadPool.submit(callable);
	}

	public void executeOnFireAndForgetThreadPool(Runnable r) {
		fireAndForgetThreadPool.execute(r);
	}
}
