package at.favre.app.dalitest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import at.favre.app.dalitest.R;
import at.favre.lib.dali.Dali;
import at.favre.lib.hood.Hood;
import at.favre.lib.hood.interfaces.actions.ManagerControl;


public class MainMenuActivity extends AppCompatActivity {
    private ManagerControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_blur1).setOnClickListener(new StartActivityListener(this, 0));
        findViewById(R.id.btn_liveblur1).setOnClickListener(new StartActivityListener(this, 1));
        findViewById(R.id.btn_animation1).setOnClickListener(new StartActivityListener(this, 2));
        findViewById(R.id.btn_viewblur).setOnClickListener(new StartActivityListener(this, 3));
        findViewById(R.id.btn_blur2).setOnClickListener(new StartActivityListener(this, 4));
        findViewById(R.id.btn_blur_misc).setOnClickListener(new StartActivityListener(this, 5));
        findViewById(R.id.btn_navdrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, NavigationDrawerActivity.class));
            }
        });

        findViewById(R.id.btn_clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dali.resetAndSetNewConfig(MainMenuActivity.this, new Dali.Config());
                Dali.setDebugMode(true);
            }
        });

        control = Hood.ext().registerShakeToOpenDebugActivity(getApplicationContext(),
                DebugActivity.createIntent(this, DebugActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        control.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        control.stop();
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
            i.putExtra(GenericActivity.FRAGMENT_ID, fragmentId);
            activity.startActivity(i);
        }
    }
}
