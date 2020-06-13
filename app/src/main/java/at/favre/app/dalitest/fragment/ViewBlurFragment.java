package at.favre.app.dalitest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import at.favre.app.dalitest.R;
import at.favre.app.dalitest.databinding.FragmentViewblurBinding;
import at.favre.lib.dali.Dali;

public class ViewBlurFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentViewblurBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewblur, container, false);

        final Dali dali = Dali.create(getActivity());
        dali.load(binding.blurTemplateView).blurRadius(20).downScale(2).concurrent().reScale().skipCache().into(binding.blurView);
        dali.load(binding.blurTemplateView2).blurRadius(20).downScale(1).concurrent().reScale().skipCache().into(binding.blurView2);

        binding.btnRerender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dali.load(binding.blurTemplateView2).blurRadius(20).downScale(1).reScale().noFade().skipCache().into(binding.blurView2);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
