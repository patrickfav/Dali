package at.favre.lib.dali.blur;

import androidx.renderscript.RenderScript;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.dali.blur.algorithms.RenderScriptGaussianBlur;

/**
 * Enum of all supported algorithms
 *
 * @author pfavre
 */
public enum EBlurAlgorithm {
    RS_GAUSS_FAST, RS_BOX_5x5,
    RS_GAUSS_5x5, RS_STACKBLUR, STACKBLUR,
    GAUSS_FAST, BOX_BLUR, NONE;

    public static List<EBlurAlgorithm> getAllAlgorithms() {
        List<EBlurAlgorithm> algorithms = new ArrayList<EBlurAlgorithm>();
        for (EBlurAlgorithm algorithm : values()) {
            if (!algorithm.equals(NONE)) {
                algorithms.add(algorithm);
            }
        }
        return algorithms;
    }

    public static IBlur createDefaultBlur(RenderScript rs) {
        return new RenderScriptGaussianBlur(rs);
    }
}
