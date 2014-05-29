package at.favre.app.dalitest;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.animation.KeyFrameBlur;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		getSupportActionBar().show();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LiveBlurFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			ImageView iv = (ImageView) rootView.findViewById(R.id.image);
			iv.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).get());

			List<KeyFrameBlur.KeyFrameConfig> keyFrameConfigs = new ArrayList<KeyFrameBlur.KeyFrameConfig>();
			keyFrameConfigs.add(new KeyFrameBlur.KeyFrameConfig(2,4));
			keyFrameConfigs.add(new KeyFrameBlur.KeyFrameConfig(2,8));
			keyFrameConfigs.add(new KeyFrameBlur.KeyFrameConfig(4,8));
			keyFrameConfigs.add(new KeyFrameBlur.KeyFrameConfig(8,8));
			keyFrameConfigs.add(new KeyFrameBlur.KeyFrameConfig(8,16));

			final TransitionDrawable transitionDrawable = new KeyFrameBlur(Dali.create(getActivity()),new ImageReference(R.drawable.test_img1).synchronouslyLoadBitmap(getResources()),keyFrameConfigs);
			transitionDrawable.setCrossFadeEnabled(true);

			iv.setImageDrawable(transitionDrawable);

			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});

//			ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
//			iv2.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).brightness(10).frostedGlass().get());
//
//			ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
//			iv3.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(2).reScaleIfDownscaled().get());
//
//			ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
//			iv4.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(3).reScaleIfDownscaled().get());

			return rootView;
        }
    }
}
