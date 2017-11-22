package at.favre.lib.dali.builder;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.util.BenchmarkUtil;

/**
 * A simple Profiler that helps in measuring parts of code.
 * <p>
 * This will use nano seconds (if possible with SDK)
 */
public class PerformanceProfiler {
    private static final String TAG = PerformanceProfiler.class.getSimpleName();

    private String description;
    private List<Duration> durations;
    private boolean isActivated;

    public PerformanceProfiler(String description) {
        this(description, true);
    }

    /**
     * @param description describes the task
     * @param isActivated if this is false, every call to a method will do exactly nothing to
     *                    avoid to have unnecessary overhead
     */
    public PerformanceProfiler(String description, boolean isActivated) {
        this.description = description;
        this.durations = new ArrayList<Duration>();
        this.isActivated = isActivated;
    }

    /**
     * Start a task. The id is needed to end the task
     *
     * @param id
     * @param taskName
     */
    public void startTask(int id, String taskName) {
        if (isActivated) {
            durations.add(new Duration(id, taskName, BenchmarkUtil.elapsedRealTimeNanos()));
        }
    }

    public void endTask(int id) {
        endTask(id, null);
    }

    public void endTask(int id, String additionalInfo) {
        if (isActivated) {
            for (Duration duration : durations) {
                if (duration.getId() == id) {
                    duration.setEndTimstamp(BenchmarkUtil.elapsedRealTimeNanos());
                    if (additionalInfo != null) {
                        duration.setAdditionalInfo(additionalInfo);
                    }
                    return;
                }
            }
            Log.w(TAG, "Could not find task with id " + id);
        }
    }

    /**
     * Returns the duration of the measured tasks in ms
     */
    public double getDurationMs() {
        double durationMs = 0;
        for (Duration duration : durations) {
            if (duration.taskFinished()) {
                durationMs += duration.getDurationMS();
            }
        }
        return durationMs;
    }

    public void printResultToLog(String tag) {
        if (isActivated) {
            StringBuilder sb = new StringBuilder("->\nLog profile for task " + description + " - " + BenchmarkUtil.formatNum(getDurationMs(), "0.##") + "ms\n----------------------------------------\n");
            for (Duration duration : durations) {
                String logMsg = " * " + duration.getTaskDescription();
                if (duration.getAdditionalInfo() != null && !duration.getAdditionalInfo().isEmpty()) {
                    logMsg += " / " + duration.getAdditionalInfo();
                }
                if (duration.taskFinished()) {
                    logMsg += " - " + BenchmarkUtil.formatNum(duration.getDurationMS(), "0.##") + "ms";
                } else {
                    logMsg += " - unfinished";
                }
                sb.append(logMsg + "\n");
            }

            Log.d(tag, sb.toString());
        }
    }

    public void printResultToLog() {
        printResultToLog(TAG);
    }

    public static class Duration {
        private int id;
        private String taskDescription;
        private String additionalInfo;
        private long startTimestamp;
        private long duration = -1;

        public Duration(int id, String taskDescription, long startTimestamp) {
            this.id = id;
            this.taskDescription = taskDescription;
            this.startTimestamp = startTimestamp;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
        }

        public void setEndTimstamp(long endTimstamp) {
            duration = endTimstamp - startTimestamp;
        }

        public long getDuration() {
            return duration;
        }

        public double getDurationMS() {
            return duration / 1000000d;
        }

        public boolean taskFinished() {
            return duration != -1;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public int getId() {
            return id;
        }
    }

}
