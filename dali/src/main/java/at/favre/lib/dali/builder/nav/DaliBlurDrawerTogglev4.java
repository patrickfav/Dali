package at.favre.lib.dali.builder.nav;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * This is a {@link ActionBarDrawerToggle} version of the {@link DaliBlurDrawerToggle}. It is deprecated and
 * you should use {@link DaliBlurDrawerToggle} instead. This is only for backwards compatibility.
 */
@Deprecated
public class DaliBlurDrawerTogglev4 extends ActionBarDrawerToggle {
    private static String TAG = DaliBlurDrawerTogglev4.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private Dali dali;
    private ImageView blurView;
    private int blurRadius = 16;
    private int downSample = 4;
    private CacheMode cacheMode = CacheMode.AUTO;
    private boolean forceRedraw = false;
    private boolean enableBlur = true;

    public DaliBlurDrawerTogglev4(Activity activity, DrawerLayout drawerLayout, @DrawableRes int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        dali = Dali.create(drawerLayout.getContext());
        this.drawerLayout = drawerLayout;
    }

    /**
     * @param blurRadius
     * @param downSample
     * @param cacheMode  AUTO = reblur occurs everytime the drawer gets opened,
     *                   MANUAL = user has to call {@link DaliBlurDrawerTogglev4#setForceRedraw()} for a reblur (can be better optimized if the view hardly changes
     */
    public void setConfig(int blurRadius, int downSample, CacheMode cacheMode) {
        BuilderUtil.checkBlurRadiusPrecondition(blurRadius);
        this.blurRadius = blurRadius;
        this.downSample = downSample;
        this.cacheMode = cacheMode;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        renderBlurLayer(slideOffset);
    }

    /**
     * This will blur the view behind it and set it in
     * a imageview over the content with a alpha value
     * that corresponds to slideOffset.
     */
    private void renderBlurLayer(float slideOffset) {
        if (enableBlur) {
            if (slideOffset == 0 || forceRedraw) {
                clearBlurView();
            }

            if (slideOffset > 0f && blurView == null) {
                if (drawerLayout.getChildCount() == 2) {
                    blurView = new ImageView(drawerLayout.getContext());
                    blurView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    blurView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    drawerLayout.addView(blurView, 1);
                }

                if (BuilderUtil.isOnUiThread()) {
                    if (cacheMode.equals(CacheMode.AUTO) || forceRedraw) {
                        dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).noFade().error(Dali.NO_RESID).concurrent().skipCache().into(blurView);
                        forceRedraw = false;
                    } else {
                        dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).noFade().error(Dali.NO_RESID).concurrent().into(blurView);
                    }
                }
            }

            if (slideOffset > 0f && slideOffset < 1f) {
                int alpha = (int) Math.ceil((double) slideOffset * 255d);
                LegacySDKUtil.setImageAlpha(blurView, alpha);
            }
        }
    }

    private void clearBlurView() {
        if (drawerLayout.getChildCount() == 3) {
            drawerLayout.removeViewAt(1);
        }
        blurView = null;
    }

    /**
     * Syncs state after e.g. orientation change
     */
    @Override
    public void syncState() {
        super.syncState();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            renderBlurLayer(255);
        } else {
            renderBlurLayer(0);
        }

    }

    /**
     * If this is set to false, it will basically behave like a normal
     * {@link android.support.v4.app.ActionBarDrawerToggle}. Use this
     * to deactivate the effect on slower devices.
     *
     * @param enableBlur
     */
    public void setEnableBlur(boolean enableBlur) {
        this.enableBlur = enableBlur;
    }

    /**
     * This will invalidate any cache and force
     * a re-blur on the next drawer slide event.
     * This is used with {@link DaliBlurDrawerTogglev4.CacheMode#MANUAL}
     */
    public void setForceRedraw() {
        forceRedraw = true;
    }

    public enum CacheMode {AUTO, MANUAL}
}
