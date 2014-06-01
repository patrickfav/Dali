package at.favre.lib.dali.builder.nav;

import android.app.Activity;
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
 * This is a {@link android.support.v4.app.ActionBarDrawerToggle} that
 * will blur the content behind when the nav drawer is opened. It uses
 * a third layer over the content layer that alpha transitions in & out.
 *
 * It features 2 Cachemodes: {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#AUTO} and {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#MANUAL}
 * The difference is, that in AUTO mode the blurview will be redrawn everytime
 * the nav drawer opens. In manual you can decide for yourself when the cache
 * will be invalidated. So if you hav
 */
public class DaliBlurDrawerToggle extends ActionBarDrawerToggle {
	private static String TAG = DaliBlurDrawerToggle.class.getSimpleName();
	private DrawerLayout drawerLayout;
	private Dali dali;
	private ImageView blurView;

	public enum CacheMode {AUTO, MANUAL}

	private int blurRadius = 16;
	private int downSample = 4;
	private CacheMode cacheMode = CacheMode.AUTO;
	private boolean forceRedraw = false;
	private boolean enableBlur = true;


	public DaliBlurDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
		super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
		dali= Dali.create(drawerLayout.getContext());
		this.drawerLayout=drawerLayout;
	}

	/**
	 * @param blurRadius
	 * @param downSample
	 * @param cacheMode AUTO = reblur occurs everytime the drawer gets opened,
	 *                  MANUAL = user has to call {@link DaliBlurDrawerToggle#setForceRedraw()} for a reblur (can be better optimized if the view hardly changes
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
		if(enableBlur) {
			if (slideOffset <= 0f || forceRedraw) {
				if (drawerLayout.getChildCount() == 3) {
					drawerLayout.removeViewAt(1);
					blurView = null;
				}
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
						dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).error(Dali.NO_IMAGE_RESID).skipCache().into(blurView);
						forceRedraw = false;
					} else {
						dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).error(Dali.NO_IMAGE_RESID).into(blurView);
					}
				}
			}

			if (slideOffset > 0f && slideOffset < 1f) {
				int alpha = (int) Math.ceil((double) slideOffset * 255d);
				LegacySDKUtil.setImageAlpha(blurView, alpha);
			}
		}
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
	 * This is used with {@link at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle.CacheMode#MANUAL}
	 */
	public void setForceRedraw() {
		forceRedraw = true;
	}
}
