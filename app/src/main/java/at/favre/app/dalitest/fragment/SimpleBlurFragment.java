package at.favre.app.dalitest.fragment;

import android.graphics.Color;
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
import at.favre.lib.dali.builder.blur.BlurBuilder;

public class SimpleBlurFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSimpleBlurBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_simple_blur, container, false);

        Dali dali = Dali.create(getActivity());

        final ImageView iv = binding.image;
        BlurBuilder.JobDescription jobDescription1 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).colorFilter(Color.parseColor("#ff00529c")).concurrent().into(iv);
        binding.subtitle1.setText(jobDescription1.builderDescription);

        final ImageView iv2 = binding.image2;
        BlurBuilder.JobDescription jobDescription2 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).brightness(0).concurrent().into(iv2);
        binding.subtitle2.setText(jobDescription2.builderDescription);

        final ImageView iv3 = binding.image3;
        BlurBuilder.JobDescription jobDescription3 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12).downScale(1).colorFilter(Color.parseColor("#ffccdceb")).concurrent().reScale().into(iv3);
        binding.subtitle3.setText(jobDescription3.builderDescription);

        final ImageView iv4 = binding.image4;
        BlurBuilder.JobDescription jobDescription4 = dali.load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(8).downScale(4).brightness(-40).concurrent().reScale().into(iv4);
        binding.subtitle4.setText(jobDescription4.builderDescription);

        return binding.getRoot();
    }
}
