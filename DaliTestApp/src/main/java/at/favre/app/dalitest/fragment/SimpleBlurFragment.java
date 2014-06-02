package at.favre.app.dalitest.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import at.favre.app.dalitest.R;
import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.blur.BlurBuilder;

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
		BlurBuilder.JobDescription jobDescription1 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).colorFilter(Color.parseColor("#ff00529c")).concurrent().into(iv);
		((TextView) rootView.findViewById(R.id.subtitle1)).setText(jobDescription1.builderDescription);

		final ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
		BlurBuilder.JobDescription jobDescription2 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).brightness(0).concurrent().into(iv2);
		((TextView) rootView.findViewById(R.id.subtitle2)).setText(jobDescription2.builderDescription);


		final ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
		BlurBuilder.JobDescription jobDescription3 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).downScale(1).colorFilter(Color.parseColor("#ffccdceb")).concurrent().reScale().into(iv3);
		((TextView) rootView.findViewById(R.id.subtitle3)).setText(jobDescription3.builderDescription);


		final ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
		BlurBuilder.JobDescription jobDescription4 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(8).downScale(4).brightness(-40).concurrent().reScale().into(iv4);
		((TextView) rootView.findViewById(R.id.subtitle4)).setText(jobDescription4.builderDescription);


		return rootView;
	}
}
