package at.favre.lib.dali.builder;

import android.content.Context;
import android.content.res.Resources;

import androidx.renderscript.RenderScript;

/**
 * Created by PatrickF on 26.05.2014.
 */
public class ContextWrapper {
    private Context context;
    private RenderScript renderScript;
    private RenderScript.ContextType renderScriptContextType = RenderScript.ContextType.NORMAL;

    public ContextWrapper(Context context) {
        this.context = context;
    }

    public ContextWrapper(Context context, RenderScript.ContextType renderScriptContextType) {
        this.context = context;
        this.renderScriptContextType = renderScriptContextType;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Syncronously creates a Renderscript context if none exists.
     * Creating a Renderscript context takes about 20 ms in Nexus 5
     *
     * @return
     */
    public RenderScript getRenderScript() {
        if (renderScript == null) {
            renderScript = RenderScript.create(context, renderScriptContextType);
        }
        return renderScript;
    }

    public Resources getResources() {
        return context.getResources();
    }
}
