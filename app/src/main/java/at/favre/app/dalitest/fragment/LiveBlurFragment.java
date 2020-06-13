package at.favre.app.dalitest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import at.favre.app.dalitest.R;
import at.favre.app.dalitest.databinding.FragmentLiveblurBinding;
import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.live.LiveBlurWorker;
import at.favre.lib.dali.view.ObservableScrollView;
import at.favre.lib.dali.view.Observers;

/**
 * Simple example on how the live blur can be implemented
 * <p>
 * This is a view pager with some different views (images, scrollview, list view)
 * to show how a live blur can be implemented in these situations
 */
public class LiveBlurFragment extends Fragment {
    private FragmentLiveblurBinding binding;
    private LiveBlurWorker blurWorker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_liveblur, container, false);

        binding.pager.setAdapter(new ScreenSlidePagerAdapter());
        blurWorker = Dali.create(getActivity()).liveBlur(binding.blurTemplateView, binding.topBlurView, binding.bottomBlurView).downScale(8).assemble(true);
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                blurWorker.updateBlurView();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return binding.getRoot();
    }

    private class ScreenSlidePagerAdapter extends PagerAdapter {

        private FrameLayout scrollViewLayout;
        private FrameLayout listViewLayout;

        public View getView(int position, ViewPager pager) {
            switch (position) {
                case 0:
                    return createImageView(R.drawable.photo3_med);
                case 1:
                    return createImageView(R.drawable.photo2_med);
                case 2:
                    return createImageView(R.drawable.photo4_med);
                case 3:
                    return createScrollView();
                case 4:
                    return createListView();

                default:
                    return createImageView(R.drawable.photo1_med);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewPager pager = (ViewPager) container;
            View view = getView(position, pager);

            pager.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        public View createImageView(int drawableResId) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT));
            Picasso.with(getActivity()).load(drawableResId).into(imageView);
            return imageView;
        }

        /**
         * Creates a ScrollView (used with as simple {@link at.favre.lib.dali.view.ObservableScrollView} to be able to attach a scrollListener)
         * that updates the blurview when it gets scrolled
         */
        public View createScrollView() {
            if (scrollViewLayout == null) {
                scrollViewLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.inc_scrollview, binding.pager, false);
                ((ObservableScrollView) scrollViewLayout.findViewById(R.id.scrollview)).setScrollViewListener(new Observers.ScrollViewListener() {
                    @Override
                    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                        blurWorker.updateBlurView();
                    }
                });
            }
            return scrollViewLayout;
        }

        /**
         * Creates a ListView with a scrollListener that updates the blur view
         */
        public View createListView() {
            if (listViewLayout == null) {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 20; i++) {
                    list.add("This is a long line of text and so on " + i);
                }
                listViewLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.inc_listview, binding.pager, false);
                ((ListView) listViewLayout.findViewById(R.id.listview)).setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));
                ((ListView) listViewLayout.findViewById(R.id.listview)).setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                        blurWorker.updateBlurView();
                    }
                });
            }
            return listViewLayout;
        }
    }
}
