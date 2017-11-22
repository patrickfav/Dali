package at.favre.lib.dali.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.blur.BlurWorker;
import at.favre.lib.dali.util.BuilderUtil;

public class ExecutorManager {
    private static final int MAX_QUEUE = 25;

    public enum ThreadPoolType {
        SERIAL, CONCURRENT
    }

    private ThreadPoolExecutor serialThreadPool;
    private ThreadPoolExecutor concurrentThreadPool;
    private ThreadPoolExecutor fireAndForgetThreadPool;

    private Map<String, List<Future<BlurWorker.Result>>> taskList;

    public ExecutorManager(int maxConcurrentMainWorkers) {
        concurrentThreadPool = new ThreadPoolExecutor(maxConcurrentMainWorkers, maxConcurrentMainWorkers, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));
        concurrentThreadPool.allowCoreThreadTimeOut(true);

        serialThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));

        fireAndForgetThreadPool = new ThreadPoolExecutor(4, 4, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE));
        fireAndForgetThreadPool.allowCoreThreadTimeOut(true);

        taskList = new ConcurrentHashMap<String, List<Future<BlurWorker.Result>>>();
    }

    public Future<BlurWorker.Result> submitThreadPool(Callable<BlurWorker.Result> callable, String tag, ThreadPoolType type) {
        Future<BlurWorker.Result> future = null;

        if (type.equals(ThreadPoolType.CONCURRENT)) {
            future = concurrentThreadPool.submit(callable);
        } else {
            future = serialThreadPool.submit(callable);
        }

        if (!taskList.containsKey(tag)) {
            taskList.put(tag, new ArrayList<Future<BlurWorker.Result>>());
        }

        taskList.get(tag).add(future);

        removeDoneTasks();

        return future;
    }

    /**
     * Cancel all task with this tag and returns the canceled task count
     *
     * @param tagToCancel
     * @return
     */
    public synchronized int cancelByTag(String tagToCancel) {
        int i = 0;
        if (taskList.containsKey(tagToCancel)) {
            removeDoneTasks();

            for (Future<BlurWorker.Result> future : taskList.get(tagToCancel)) {
                BuilderUtil.logVerbose(Dali.getConfig().logTag, "Canceling task with tag " + tagToCancel, Dali.getConfig().debugMode);
                future.cancel(true);
                i++;
            }

            //remove all canceled tasks
            Iterator<Future<BlurWorker.Result>> iter = taskList.get(tagToCancel).iterator();
            while (iter.hasNext()) {
                if (iter.next().isCancelled()) {
                    iter.remove();
                }
            }
        }
        return i;
    }

    private void removeDoneTasks() {
        for (String tag : taskList.keySet()) {
            Iterator<Future<BlurWorker.Result>> iter = taskList.get(tag).iterator();
            while (iter.hasNext()) {
                if (iter.next().isDone()) {
                    iter.remove();
                }
            }
            if (taskList.get(tag).isEmpty()) {
                taskList.remove(tag);
            }
        }
    }

    public void executeOnFireAndForgetThreadPool(Runnable r) {
        fireAndForgetThreadPool.execute(r);
    }

    public void shutDown() {
        concurrentThreadPool.shutdown();
        serialThreadPool.shutdown();
        fireAndForgetThreadPool.shutdown();
    }
}
