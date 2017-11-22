package at.favre.lib.dali.blur;

/**
 * Blur kernels for convolve matrix algorithms
 *
 * @author pfavre
 */
public final class BlurKernels {
    private BlurKernels() {
    }

    public static final float[] GAUSSIAN_5x5 = new float[]{
            0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f,
            0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f,
            0.0219f, 0.0983f, 0.1621f, 0.0983f, 0.0219f,
            0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f,
            0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f
    };

    public static final float[] BOX_5x5 = new float[]{
            0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
            0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
            0.04f, 0.0425f, 0.05f, 0.0425f, 0.04f,
            0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
            0.04f, 0.04f, 0.04f, 0.04f, 0.04f
    };

    public static final float[] BOX_3x3 = new float[]{
            0.111111111111111111111111112f, 0.111111111111111111111111112f, 0.111111111111111111111111112f,
            0.111111111111111111111111112f, 0.13f, 0.111111111111111111111111112f,
            0.111111111111111111111111112f, 0.111111111111111111111111112f, 0.111111111111111111111111112f
    };
}
