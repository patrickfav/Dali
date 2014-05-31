package at.favre.app.dalitest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.favre.lib.dali.Dali;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class SimpleBlurFragment extends Fragment{

	public SimpleBlurFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		Dali dali = Dali.create(getActivity());

		final ImageView iv = (ImageView) rootView.findViewById(R.id.image);
		iv.setImageDrawable(dali.load(R.drawable.test_img1).blurRadius(24).get());

		final ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
		iv2.setImageDrawable(dali.load(R.drawable.test_img1).blurRadius(24).get());

		final ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
		iv3.setImageDrawable(dali.load(R.drawable.test_img1).blurRadius(24).brightness(-60).get());

		final ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
		iv4.setImageDrawable(dali.load(R.drawable.test_img1).blurRadius(24).brightness(-59).get());


		return rootView;
	}
}
