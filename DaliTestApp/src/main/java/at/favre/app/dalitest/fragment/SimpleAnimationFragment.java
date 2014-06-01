package at.favre.app.dalitest.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.favre.app.dalitest.R;
import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.animation.BlurAnimation;
import at.favre.lib.dali.builder.animation.BlurKeyFrame;
import at.favre.lib.dali.builder.animation.BlurKeyFrameManager;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class SimpleAnimationFragment extends Fragment{

	public SimpleAnimationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_simple_blur, container, false);

		final ImageView iv = (ImageView) rootView.findViewById(R.id.image);
		iv.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).get());

		Bitmap original = new ImageReference(R.drawable.test_img1).synchronouslyLoadBitmap(getResources());

		BlurKeyFrameManager man = new BlurKeyFrameManager();
		man.addKeyFrame(new BlurKeyFrame(2,4,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,8,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,12,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,16,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,20,0,300));

		final BlurAnimation animation = new BlurAnimation(getActivity(),man);
		animation.prepareAnimation(original);

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation.start(iv);
			}
		});

		final ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
		iv2.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).brightness(10).get());

		BlurKeyFrameManager man2 = BlurKeyFrameManager.createLinearKeyFrames(10,700,4,20);

		final BlurAnimation animation2 = new BlurAnimation(getActivity(),man2);
		animation2.prepareAnimation(original);

		iv2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation2.start(iv2);
			}
		});

		final ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
		iv3.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(2).reScaleIfDownscaled().get());

		BlurKeyFrameManager man3 = BlurKeyFrameManager.createLinearKeyFrames(4,700,4,20);

		final BlurAnimation animation3 = new BlurAnimation(getActivity(),man3);
		animation3.prepareAnimation(original);

		iv3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation3.start(iv3);
			}
		});

		final ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
		iv4.setImageDrawable(Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(3).reScaleIfDownscaled().get());

		BlurKeyFrameManager man4 = BlurKeyFrameManager.createLinearKeyFrames(2,700,4,20);

		final BlurAnimation animation4 = new BlurAnimation(getActivity(),man4);
		animation4.prepareAnimation(original);

		iv4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation4.start(iv4);
			}
		});

		return rootView;
	}
}
