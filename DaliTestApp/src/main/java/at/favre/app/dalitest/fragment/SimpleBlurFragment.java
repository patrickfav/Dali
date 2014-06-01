package at.favre.app.dalitest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.favre.app.dalitest.R;
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
		View rootView = inflater.inflate(R.layout.fragment_simple_blur, container, false);

		Dali dali = Dali.create(getActivity());

		final ImageView iv = (ImageView) rootView.findViewById(R.id.image);
		dali.load(R.drawable.test_img1).blurRadius(24).into(iv);


		final ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
		dali.load(R.drawable.test_img1).blurRadius(24).brightness(50).into(iv2);

		final ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
		dali.load(R.drawable.test_img1).blurRadius(12).downScale(2).reScaleIfDownscaled().into(iv3);

		final ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
		dali.load(R.drawable.test_img1).blurRadius(8).downScale(4).brightness(-40).reScaleIfDownscaled().into(iv4);

		return rootView;
	}
}
