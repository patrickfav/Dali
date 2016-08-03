# Dali

Dali is a image blur library for Android, but also enabling any other kind of image filter. There are
several modules for either static blurring, live blurring and animations.
It uses RenderScript internally and is heavily cached to be fast and keeps small footprint. It features
a lot of additional image filters and may be easily extended and pretty every configuration can be changed.

![Gallery](https://github.com/patrickfav/Dali/blob/master/misc/gallery1.png?raw=true)

*Note: This library is in prototype state and currently not maintained or developed. It is mostly
feature complete (except for the animation module) although bugs are to be expected.*

## Static Blur

Dali uses the builder pattern to manipulate. A very simple example would be:

    Dali.create(context).load(R.drawable.test_img1).blurRadius(12).into(imageView);

which would blur given image in the background and set it to the ImageView.

Inbuilt image manipulation filter are brightness, contrast and color.

Most of them use RenderScript so they should be fast. For details on the filter implementation see the
`at.favre.lib.dali.builder.processor` package.

Any other manipulation filter can be implemented through the `IBitmapProcessor` and `.addPreProcessor`
on a builder.

A more complex example is:

    Dali.create(context).load(R.drawable.test_img1).placeholder(R.drawable.test_img1).blurRadius(12)
        .downScale(2).colorFilter(Color.parseColor("#ffccdceb")).concurrent().reScale().into(iv3)

Will blur, color filter a dowscaled version of given image on a concurrent thread pool and rescales it
the target (the imageView) this case and will set a placeholder until the opartions are finished.

_Do note that `Dali.create(context)` will always create a new instace so it may be advisable to keep the reference._

For more examples see `SimpleBlurFragment.java` and `SimpleBlurBrightnessFragment.java`

### Blur any View

Apart from resource ids, bitmaps, files and InputStreams `.load(anyAndroidView)` method also loads any View as source
and blurs its drawingCache into the target view.

   		Dali.create(context).load(rootView.findViewById(R.id.blurTemplateView)).blurRadius(20)
   		    .downScale(2).concurrent().reScale().skipCache().into(imageView);

For more examples see `ViewBlurFragment.java`

### Skip blurring

If you just want to use other image filters you could use:

    Dali.create(context).load(R.drawable.test_img1).algorithm(EBlurAlgorithm.NONE).brightness(70).concurrent().into(iv);


## Live Blur

Live blur refers to an effect where it a portion of the view blurs what's behind it. It can be used with e.g.
a `ViewPager`, `Scrollview`, `RecyclerView`, etc.

![Live Blur Animation](https://github.com/patrickfav/Dali/blob/master/misc/viewpager_anim.gif?raw=true)

A very simple example with a ViewPager would be:

    blurWorker = Dali.create(getActivity()).liveBlur(rootViewPagerWrapperView,topBlurView,bottomBlurView).downScale(8).assemble(true);

    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            blurWorker.updateBlurView();
        }
        @Override public void onPageSelected(int position) {}
		@Override public void onPageScrollStateChanged(int state) {}
    });

A full example can be found in the test app's `LiveBlurFragment.java`

The idea is basically to hook up to the scrollable view and every scroll event the blur has to be updated with
`blurWorker.updateBlurView()`. Many of the views do not offer these features therefore there are simple implementations
for some views (see package `at.favre.lib.dali.view.Observable*`)


## Navigation Drawer Background Blur

A specialized version of live blur is blurring the background of a `NavigationDrawer`:

![Blur Nav Animation](https://github.com/patrickfav/Dali/blob/master/misc/blur_nav.gif?raw=true)

    protected void onCreate(Bundle savedInstanceState) {
        ...
		mDrawerToggle = new DaliBlurDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.addDrawerListener(mDrawerToggle);
        ...
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}


A full example can be found in the test app's `NavigationDrawerActivity.java`

## Blur Transition Animation

A simple framework for animating a static image from sharp to blur. It uses a keyframes to configure
the animation:

    BlurKeyFrameManager man = new BlurKeyFrameManager();
    man.addKeyFrame(new BlurKeyFrame(2,4,0,300));
    man.addKeyFrame(new BlurKeyFrame(2,8,0,300));
    ...

then an `ImageView` can be animated:

BlurKeyFrameTransitionAnimation animation = new BlurKeyFrameTransitionAnimation(getActivity(),man);
animation.start(imageView);

![Blur Animation](https://github.com/patrickfav/Dali/blob/master/misc/blur_anim.gif?raw=true)

A full example can be found in the test app's `SimpleAnimationFragment.java`

_Note: This module is not feature complete and has still terrible bugs, so use at your own risk._

## Licences

Copyright 2016 Patrick Favre-Bulle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.