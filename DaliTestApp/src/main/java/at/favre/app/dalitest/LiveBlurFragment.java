package at.favre.app.dalitest;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.LiveBlurWorker;
import at.favre.lib.dali.view.ObservableScrollView;
import at.favre.lib.dali.view.ObservableViewPager;
import at.favre.lib.dali.view.Observers;

/**
 * Simple example on how the live blur can be implemented
 *
 * This is a viewpage with some different views (images, scrollview, listview)
 * to show how a live blur can be implemented in these situations
 *
 */
public class LiveBlurFragment extends Fragment{
	private static final String TAG = LiveBlurFragment.class.getSimpleName();

	private ObservableViewPager mPager;
	private PagerAdapter mPagerAdapter;

	private View topBlurView;
	private View bottomBlurView;
	private View templateView;

	private LiveBlurWorker blurWorker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_liveblur,container,false);


		mPager = (ObservableViewPager) v.findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter();
		mPager.setAdapter(mPagerAdapter);

		templateView = v.findViewById(R.id.blurTemplateView);
		topBlurView = v.findViewById(R.id.topBlurView);
		bottomBlurView = v.findViewById(R.id.bottomBlurView);

		blurWorker = Dali.create(getActivity(),true).liveBlur(templateView,topBlurView,bottomBlurView).downSample(8).assemble();

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		mPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				return true;
			}
		});

		mPager.setOnDrawListener(new Observers.DrawListener() {
			@Override
			public void onDraw(Canvas canvas) {
				blurWorker.updateBlurView();
			}
		});

		return v;
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
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,ViewPager.LayoutParams.MATCH_PARENT));
			Picasso.with(getActivity()).load(drawableResId).into(imageView);
			return imageView;
		}

		/**
		 * Creates a ScrollView (used with as simple {@link at.favre.lib.dali.view.ObservableScrollView} to be able to attach a scrollListener)
		 * that updates the blurview when it gets scrolled
		 */
		public View createScrollView() {
			if(scrollViewLayout == null) {
				scrollViewLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.inc_scrollview, mPager, false);
				((ObservableScrollView) scrollViewLayout.findViewById(R.id.scrollview)).setScrollViewListener(new Observers.ScrollViewListener() {
					@Override
					public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
						blurWorker.updateBlurView();
					}
				});
				Picasso.with(getActivity()).load(R.drawable.photo1_med).into((ImageView) scrollViewLayout.findViewById(R.id.photo1));
				Picasso.with(getActivity()).load(R.drawable.photo2_med).into((ImageView) scrollViewLayout.findViewById(R.id.photo2));
			}
			return scrollViewLayout;
		}

		/**
		 * Creates a ListView with a scrollListener that updates the blurview
		 */
		public View createListView() {
			if(listViewLayout == null) {
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < 20; i++) {
					list.add("This is a long line of text and so on "+i);
				}
				listViewLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.inc_listview, mPager, false);
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
