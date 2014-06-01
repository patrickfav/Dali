package at.favre.lib.dali.builder.nav;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.util.BuilderUtil;
import at.favre.lib.dali.util.LegacySDKUtil;

/**
 * Created by PatrickF on 01.06.2014.
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

	public DaliBlurDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
		super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
		dali= Dali.create(drawerLayout.getContext());
		this.drawerLayout=drawerLayout;
	}

	public void setConfig(int blurRadius, int downSample, CacheMode cacheMode) {
		BuilderUtil.checkBlurRadiusPrecondition(blurRadius);
		this.blurRadius = blurRadius;
		this.downSample = downSample;
		this.cacheMode = cacheMode;
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		super.onDrawerSlide(drawerView,slideOffset);

		if(slideOffset <= 0f || forceRedraw) {
			if(drawerLayout.getChildCount() == 3) {
				drawerLayout.removeViewAt(1);
				blurView=null;
			}
		}

		if(slideOffset > 0f && blurView == null) {
			if(drawerLayout.getChildCount() == 2) {
				blurView = new ImageView(drawerLayout.getContext());
				blurView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				blurView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				drawerLayout.addView(blurView,1);
			}

			if(cacheMode.equals(CacheMode.AUTO) || forceRedraw) {
				dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).skipCache().into(blurView);
				forceRedraw=false;
			} else {
				dali.load(drawerLayout.getChildAt(0)).blurRadius(blurRadius).downScale(downSample).into(blurView);
			}
		}

		if(slideOffset > 0f && slideOffset < 1f) {
			int alpha = (int) Math.ceil((double)slideOffset * 255d);
			LegacySDKUtil.setImageAlpha(blurView, alpha);
		}
	}

	public void setForceRedraw() {
		forceRedraw = true;
	}
}
