package at.favre.lib.dali.util;

import android.os.Build;
import android.os.SystemClock;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class BenchmarkUtil {
    private static final DecimalFormat format = new DecimalFormat("#.0");
    private static final String fileSeperator = ";";

    static {
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        format.setRoundingMode(RoundingMode.HALF_UP);
    }

    private BenchmarkUtil() {
    }

    public static long elapsedRealTimeNanos() {
        if (Build.VERSION.SDK_INT >= 17) {
            return SystemClock.elapsedRealtimeNanos();
        }
        return SystemClock.elapsedRealtime() * 1000000L;
    }

    public static String formatNum(double number) {
        return format.format(number);
    }

    public static String formatNum(double number, String formatString) {
        final DecimalFormat format = new DecimalFormat(formatString);
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(number);
    }

    public static String getScalingUnitByteSize(int byteSize) {
        double scaledByteSize = (double) byteSize;
        String unit = "byte";

        if (scaledByteSize < 1024) {
            return formatNum(scaledByteSize, "0.##") + unit;
        } else {
            unit = "KiB";
            scaledByteSize /= 1024d;

            if (scaledByteSize < 1024) {
                return formatNum(scaledByteSize, "0.##") + unit;
            } else {
                unit = "MiB";
                scaledByteSize /= 1024d;
                if (scaledByteSize < 1024) {
                    return formatNum(scaledByteSize, "0.##") + unit;
                } else {
                    unit = "GiB";
                    scaledByteSize /= 1024d;
                    return formatNum(scaledByteSize, "0.##") + unit;
                }
            }
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS", Locale.getDefault());
        return sdf.format(new Date());
    }
}
