package at.favre.app.dalitest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import at.favre.app.dalitest.R;


public class MainMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

		findViewById(R.id.btn_blur1).setOnClickListener(new StartActivityListener(this,0));
		findViewById(R.id.btn_liveblur1).setOnClickListener(new StartActivityListener(this,1));
		findViewById(R.id.btn_animation1).setOnClickListener(new StartActivityListener(this,2));
		findViewById(R.id.btn_viewblur).setOnClickListener(new StartActivityListener(this,3));
		findViewById(R.id.btn_navdrawer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainMenuActivity.this,NavigationDrawerActivity.class));
			}
		});
    }

	private static class StartActivityListener implements View.OnClickListener {
		private int fragmentId;
		private Activity activity;

		private StartActivityListener(Activity ctx, int fragmentId) {
			this.fragmentId = fragmentId;
			this.activity = ctx;
		}

		@Override
		public void onClick(View view) {
			Intent i = new Intent(activity, GenericActivity.class);
			i.putExtra(GenericActivity.FRAGMENT_ID,fragmentId);
			activity.startActivity(i);
		}
	}
}
