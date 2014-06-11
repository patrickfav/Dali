package at.favre.app.dalitest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import at.favre.app.dalitest.R;
import at.favre.app.dalitest.fragment.LiveBlurFragment;
import at.favre.app.dalitest.fragment.SimpleAnimationFragment;
import at.favre.app.dalitest.fragment.SimpleBlurBrightnessFragment;
import at.favre.app.dalitest.fragment.SimpleBlurFragment;
import at.favre.app.dalitest.fragment.SimpleBlurPlaygroundFragment;
import at.favre.app.dalitest.fragment.ViewBlurFragment;


public class GenericActivity extends ActionBarActivity {
	public final static String FRAGMENT_ID = "FRAGMENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		getSupportActionBar().show();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, getById(getIntent().getIntExtra(FRAGMENT_ID,0)))
                    .commit();
        }
    }


	private Fragment getById(int id) {
		switch (id) {
			case 0:
				return new SimpleBlurFragment();
			case 1:
				return new LiveBlurFragment();
			case 2:
				return new SimpleAnimationFragment();
			case 3:
				return new ViewBlurFragment();
			case 4:
				return new SimpleBlurBrightnessFragment();
			case 5:
				return new SimpleBlurPlaygroundFragment();
			default:
				return null;
		}
	}





}
