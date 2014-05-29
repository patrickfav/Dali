package at.favre.lib.dali.util;

import android.util.Log;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class LogUtil {

	public static void logDebug(String tag, String msg, boolean shouldLog) {
		if(shouldLog) {
			Log.d(tag, msg);
		}
	}
}
