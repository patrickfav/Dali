package at.favre.app.dalitest.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import at.favre.app.dalitest.R;
import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.animation.BlurKeyFrame;
import at.favre.lib.dali.builder.animation.BlurKeyFrameManager;
import at.favre.lib.dali.builder.animation.BlurKeyFrameTransitionAnimation;

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
		Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).into(iv);

		final Bitmap original = new ImageReference(R.drawable.test_img1).synchronouslyLoadBitmap(getResources());

		BlurKeyFrameManager man = new BlurKeyFrameManager();
		man.addKeyFrame(new BlurKeyFrame(2,4,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,8,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,12,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,16,0,300));
		man.addKeyFrame(new BlurKeyFrame(2,20,0,300));

		final BlurKeyFrameTransitionAnimation animation = new BlurKeyFrameTransitionAnimation(getActivity(),man);
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation.start(iv);
			}
		});
		((TextView) rootView.findViewById(R.id.subtitle1)).setText(man.toString());




		final ImageView iv2 = (ImageView) rootView.findViewById(R.id.image2);
		Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(24).brightness(0).noFade().into(iv2);

		BlurKeyFrameManager man2 = BlurKeyFrameManager.createLinearKeyFrames(8,700,4,20,1000);

		final BlurKeyFrameTransitionAnimation animation2 = new BlurKeyFrameTransitionAnimation(getActivity(),man2);

		iv2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation2.start(iv2);
			}
		});

		((TextView) rootView.findViewById(R.id.subtitle2)).setText(man2.toString());




		final ImageView iv3 = (ImageView) rootView.findViewById(R.id.image3);
		Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(2).reScale().into(iv3);

		BlurKeyFrameManager man3 = BlurKeyFrameManager.createLinearKeyFrames(4,1000,4,20,-80);

		final BlurKeyFrameTransitionAnimation animation3 = new BlurKeyFrameTransitionAnimation(getActivity(),man3);

		iv3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation3.start(iv3);
			}
		});

		((TextView) rootView.findViewById(R.id.subtitle3)).setText(man3.toString());


		final ImageView iv4 = (ImageView) rootView.findViewById(R.id.image4);
		Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(3).reScale().into(iv4);

		BlurKeyFrameManager man4 = BlurKeyFrameManager.createLinearKeyFrames(2,10000,4,20,-80);

		final BlurKeyFrameTransitionAnimation animation4 = new BlurKeyFrameTransitionAnimation(getActivity(),man4);

		iv4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				animation4.start(iv4);
			}
		});

		((TextView) rootView.findViewById(R.id.subtitle4)).setText(man4.toString());


		new Thread(new Runnable() {
			@Override
			public void run() {
				animation.prepareAnimation(original);
				animation2.prepareAnimation(original);
				animation3.prepareAnimation(original);
				animation4.prepareAnimation(original);
			}
		}).start();

		return rootView;
	}
}
