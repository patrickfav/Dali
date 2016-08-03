# Dali

Dali is a image blur library for Android, but also enabling any other kind of image filter. It uses
RenderScript internally and is heavily cached to be fast and keeps small footprint. It features
a lot of additional image filters and may be easily extended and pretty every configuration can be changed.

![Gallery](https://github.com/patrickfav/Dali/tree/master/misc/gallery1.png)

*Note: This library is in prototype state and currently not maintained or developed. It is mostly
feature complete altough bugs are to be expected.*

## Blur Builder

Dali uses the builder pattern to manipulate. A very simple example would be:

    Dali.create(context).load(R.drawable.test_img1).blurRadius(12).into(imageView);

which would blur given image in the background and set it to the ImageView.

Inbuilt image manipulation filter are:

   * blur
   * brightness
   * contrast
   * color

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

## Blur Transition Animation

## Navigation Drawer Background Blur

![Blur Nav Animation](https://github.com/patrickfav/Dali/tree/master/misc/blur_nav.gif)

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