package at.favre.lib.dali.builder.nav;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * This is a {@link androidx.appcompat.app.ActionBarDrawerToggle} that
 * will blur the content behind when the nav drawer is opened. It uses
 * a third layer over the content layer that alpha transitions in and out.
 * <p/>
 * It features 2 Cachemodes: {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#AUTO} and {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#MANUAL}
 * The difference is, that in AUTO mode the blurview will be redrawn everytime
 * the nav drawer opens. In manual you can decide for yourself when the cache
 * will be invalidated.
 */
public class DaliBlurDrawerToggle extends ActionBarDrawerToggle {
    private static String TAG = DaliBlurDrawerToggle.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private Dali dali;
    private ImageView blurView;
    private int blurRadius = 16;
    private int downSample = 4;
    private CacheMode cacheMode = CacheMode.AUTO;
    private boolean forceRedraw = false;
    private boolean enableBlur = true;
    private NavigationDrawerListener listener;

    public DaliBlurDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                                int openDrawerContentDescRes, int closeDrawerContentDescRes, NavigationDrawerListener listener) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        dali = Dali.create(drawerLayout.getContext());
        this.drawerLayout = drawerLayout;
        this.listener = listener;
    }

    /**
     * @param blurRadius
     * @param downSample
     * @param cacheMode  AUTO = reblur occurs everytime the drawer gets opened,
     *                   MANUAL = user has to call {@link DaliBlurDrawerToggle#setForceRedraw()} for a reblur (can be better optimized if the view hardly changes
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
     * {@link ActionBarDrawerToggle}. Use this
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
     * This is used with {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#MANUAL}
     */
    public void setForceRedraw() {
        forceRedraw = true;
    }

    public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        if (listener != null) listener.onDrawerOpened(view);
    }

    /**
     * Called when a drawer has settled in a completely open state.
     */
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (listener != null) listener.onDrawerClosed(drawerView);
    }

    public enum CacheMode {
        AUTO, MANUAL
    }
}
