package at.favre.lib.dali.util;

import android.content.Context;
import android.support.v8.renderscript.RenderScript;

import at.favre.lib.dali.blur.EBlurAlgorithm;
import at.favre.lib.dali.blur.IBlur;
import at.favre.lib.dali.blur.algorithms.BoxBlur;
import at.favre.lib.dali.blur.algorithms.GaussianFastBlur;
import at.favre.lib.dali.blur.algorithms.IgnoreBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptBox5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussian5x5Blur;
import at.favre.lib.dali.blur.algorithms.RenderScriptGaussianBlur;
import at.favre.lib.dali.blur.algorithms.RenderScriptStackBlur;
import at.favre.lib.dali.blur.algorithms.StackBlur;
import at.favre.lib.dali.builder.ContextWrapper;

/**
 * Created by PatrickF on 29.05.2014.
 */
public class BlurUtil {

	public static IBlur getIBlurAlgorithm(EBlurAlgorithm algorithm, ContextWrapper contextWrapper) {
		RenderScript rs= contextWrapper.getRenderScript();
		Context ctx = contextWrapper.getContext();

		switch (algorithm) {
			case RS_GAUSS_FAST:
				return new RenderScriptGaussianBlur(rs);
			case RS_BOX_5x5:
				return new RenderScriptBox5x5Blur(rs);
			case RS_GAUSS_5x5:
				return new RenderScriptGaussian5x5Blur(rs);
			case RS_STACKBLUR:
				return new RenderScriptStackBlur(rs, ctx);
			case STACKBLUR:
				return new StackBlur();
			case GAUSS_FAST:
				return new GaussianFastBlur();
			case BOX_BLUR:
				return new BoxBlur();
			default:
				return new IgnoreBlur();
		}
	}
}
