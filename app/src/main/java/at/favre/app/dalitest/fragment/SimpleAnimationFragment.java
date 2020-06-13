package at.favre.app.dalitest.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import at.favre.app.dalitest.R;
import at.favre.app.dalitest.databinding.FragmentSimpleBlurBinding;
import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.ImageReference;
import at.favre.lib.dali.builder.animation.BlurKeyFrame;
import at.favre.lib.dali.builder.animation.BlurKeyFrameManager;
import at.favre.lib.dali.builder.animation.BlurKeyFrameTransitionAnimation;

public class SimpleAnimationFragment extends Fragment {

    private FragmentSimpleBlurBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_simple_blur, container, false);

        Dali dali = Dali.create(getActivity());

        final ImageView iv = binding.image;
        dali.load(R.drawable.test_img1).blurRadius(24).into(iv);

        final Bitmap original = new ImageReference(R.drawable.test_img1).synchronouslyLoadBitmap(getResources());

        BlurKeyFrameManager man = new BlurKeyFrameManager();
        man.addKeyFrame(new BlurKeyFrame(2, 4, 0, 300));
        man.addKeyFrame(new BlurKeyFrame(2, 8, 0, 300));
        man.addKeyFrame(new BlurKeyFrame(2, 12, 0, 300));
        man.addKeyFrame(new BlurKeyFrame(2, 16, 0, 300));
        man.addKeyFrame(new BlurKeyFrame(2, 20, 0, 300));

        final BlurKeyFrameTransitionAnimation animation = new BlurKeyFrameTransitionAnimation(getActivity(), man);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation.start(iv);
            }
        });
        binding.subtitle1.setText(man.toString());

        final ImageView iv2 = binding.image2;
        dali.load(R.drawable.test_img1).blurRadius(24).brightness(0).noFade().into(iv2);

        BlurKeyFrameManager man2 = BlurKeyFrameManager.createLinearKeyFrames(8, 700, 4, 20, 95);

        final BlurKeyFrameTransitionAnimation animation2 = new BlurKeyFrameTransitionAnimation(getActivity(), man2);

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation2.start(iv2);
            }
        });

        binding.subtitle2.setText(man2.toString());

        final ImageView iv3 = binding.image3;
        dali.load(R.drawable.test_img1).blurRadius(12).downScale(2).reScale().into(iv3);

        BlurKeyFrameManager man3 = BlurKeyFrameManager.createLinearKeyFrames(4, 1000, 4, 20, -80);

        final BlurKeyFrameTransitionAnimation animation3 = new BlurKeyFrameTransitionAnimation(getActivity(), man3);

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation3.start(iv3);
            }
        });

        binding.subtitle3.setText(man3.toString());

        final ImageView iv4 = binding.image4;
//        Dali.create(getActivity()).load(R.drawable.test_img1).blurRadius(12).downScale(3).reScale().into(iv4);
//
//        BlurKeyFrameManager man4 = BlurKeyFrameManager.createLinearKeyFrames(2, 10000, 4, 20, -80);
//
//        final BlurKeyFrameTransitionAnimation animation4 = new BlurKeyFrameTransitionAnimation(getActivity(), man4);
//
//        iv4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animation4.start(iv4);
//            }
//        });
//
//        binding.subtitle4.setText(man4.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                animation.prepareAnimation(original);
                animation2.prepareAnimation(original);
                animation3.prepareAnimation(original);
//              animation4.prepareAnimation(original);
            }
        }).start();

        // Create red and green bitmaps to cross-fade between
        Bitmap bitmap0 = Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888);
        Bitmap bitmap1 = Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap0);
        canvas.drawColor(Color.BLUE);
        canvas = new Canvas(bitmap1);
        canvas.drawColor(Color.BLACK);
        BitmapDrawable[] drawables = new BitmapDrawable[2];
        drawables[1] = new BitmapDrawable(getResources(), dali.load(R.drawable.test_img1).brightness(0).copyBitmapBeforeProcess().blurRadius(2).skipCache().get().getBitmap());
        drawables[0] = new BitmapDrawable(getResources(), dali.load(R.drawable.test_img1).brightness(-70).copyBitmapBeforeProcess().blurRadius(2).skipCache().get().getBitmap());

//        BitmapDrawable drawables[] = new BitmapDrawable[2];
//        drawables[0] = new BitmapDrawable(getResources(), bitmap0);
//        drawables[1] = new BitmapDrawable(getResources(), bitmap1);

//        Add the red / green bitmap drawables to a TransitionDrawable.They are layered
//        in the transition drawalbe.The cross -fade effect happens by fading one out and the
//        other in.
        final TransitionDrawable crossfader = new TransitionDrawable(drawables);
        iv4.setImageDrawable(crossfader);
        crossfader.setCrossFadeEnabled(false);
//        Clicking on the drawable will cause the cross -fade effect to run.Depending on
//        which drawable is currently being shown, we either 'start' or 'reverse' the
//        transition, which determines which drawable is faded out/in during the transition.
        iv4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crossfader.reverseTransition(1);
                crossfader.startTransition(1800);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        crossfader.resetTransition();
//                    }
//                }, 1800);
            }
        });

        return binding.getRoot();
    }
}
