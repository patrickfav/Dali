package at.favre.app.dalitest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import at.favre.app.dalitest.R;
import at.favre.app.dalitest.databinding.ActivityMainMenuBinding;
import at.favre.lib.dali.Dali;
import at.favre.lib.hood.Hood;
import at.favre.lib.hood.interfaces.actions.ManagerControl;

public class MainMenuActivity extends AppCompatActivity {
    private ManagerControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityMainMenuBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        binding.btnBlur1.setOnClickListener(new StartActivityListener(this, 0));
        binding.btnLiveblur1.setOnClickListener(new StartActivityListener(this, 1));
        binding.btnAnimation1.setOnClickListener(new StartActivityListener(this, 2));
        binding.btnViewblur.setOnClickListener(new StartActivityListener(this, 3));
        binding.btnBlur2.setOnClickListener(new StartActivityListener(this, 4));
        binding.btnBlurMisc.setOnClickListener(new StartActivityListener(this, 5));
        binding.btnNavdrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, NavigationDrawerActivity.class));
            }
        });

        binding.btnClearCache.setOnClickListener(new View.OnClickListener() {
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

    private static final class StartActivityListener implements View.OnClickListener {
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
