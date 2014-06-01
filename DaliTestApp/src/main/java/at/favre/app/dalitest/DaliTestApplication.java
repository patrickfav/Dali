package at.favre.app.dalitest;

import android.app.Application;

import at.favre.lib.dali.Dali;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class DaliTestApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Dali.setDebugMode(true);
	}
}
