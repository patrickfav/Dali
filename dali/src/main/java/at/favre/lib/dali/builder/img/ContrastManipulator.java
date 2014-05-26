package at.favre.lib.dali.builder.img;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import at.favre.lib.dali.ScriptC_contrast;

/**
 * Created by PatrickF on 27.05.2014.
 */
public class ContrastManipulator implements IManipulator {
	private RenderScript rs;
	private float contrast;

	public ContrastManipulator(RenderScript rs, float contrast) {
		this.rs = rs;
		this.contrast = contrast;
	}

	@Override
	public Bitmap manipulate(Bitmap original) {
		Allocation input = Allocation.createFromBitmap(rs, original);
		final Allocation output = Allocation.createTyped(rs, input.getType());
		ScriptC_contrast mScript = new ScriptC_contrast(rs);
		mScript.invoke_setBright(contrast);
		mScript.forEach_contrast(input, output);
		output.copyTo(original);
		return original;
	}
}
