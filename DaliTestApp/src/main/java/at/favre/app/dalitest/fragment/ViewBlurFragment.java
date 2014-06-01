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
public class ViewBlurFragment extends Fragment{

	private ImageView iv,iv2;

	public ViewBlurFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_viewblur, container, false);


		iv = (ImageView) rootView.findViewById(R.id.blurView);
		iv2 = (ImageView) rootView.findViewById(R.id.blurView2);

		final Dali dali = Dali.create(getActivity());
		dali.load(rootView.findViewById(R.id.blurTemplateView)).blurRadius(20).downScale(2).reScaleIfDownscaled().skipCache().into(iv);
//		iv.setImageDrawable(dali.load(rootView.findViewById(R.id.blurTemplateView)).blurRadius(20).downScale(2).reScaleIfDownscaled().skipCache().get());
		dali.load(rootView.findViewById(R.id.blurTemplateView2)).blurRadius(20).downScale(1).reScaleIfDownscaled().skipCache().into(iv2);

		rootView.findViewById(R.id.btn_rerender).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dali.load(rootView.findViewById(R.id.blurTemplateView2)).blurRadius(20).downScale(1).reScaleIfDownscaled().skipCache().into(iv2);
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();


	}
}
