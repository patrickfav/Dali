package at.favre.lib.dali.blur;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of all supported algorithms
 *
 * @author pfavre
 */
public enum EBlurAlgorithm {
    RS_GAUSS_FAST, RS_BOX_5x5,
	RS_GAUSS_5x5, RS_STACKBLUR,STACKBLUR,
	GAUSS_FAST, BOX_BLUR, NONE;

	public static List<EBlurAlgorithm> getAllAlgorithms() {
        List<EBlurAlgorithm> algorithms = new ArrayList<EBlurAlgorithm>();
        for (EBlurAlgorithm algorithm : values()) {
            if(!algorithm.equals(NONE)) {
                algorithms.add(algorithm);
            }
        }
        return algorithms;
    }
}
