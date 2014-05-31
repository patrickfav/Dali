package at.favre.lib.dali.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class ExecutorManager {
	private static final int MAX_QUEUE = 15;

	private ExecutorService mainThreadPool;
	private ExecutorService cacheThreadPool;


	public ExecutorManager(int maxConcurrentMainWorkers) {
		mainThreadPool = new ThreadPoolExecutor(1, maxConcurrentMainWorkers,
				2500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));

		cacheThreadPool = new ThreadPoolExecutor(2, 4,500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));
	}

	public void executeOnCacheThreadPool(Runnable r) {
		cacheThreadPool.execute(r);
	}
}
