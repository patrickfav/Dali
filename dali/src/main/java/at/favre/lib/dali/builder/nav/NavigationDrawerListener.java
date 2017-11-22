package at.favre.lib.dali.builder.nav;

import android.view.View;

/**
 * Same as {@link android.support.v7.app.ActionBarDrawerToggle.DrawerToggle#onDrawerOpened(View)} and
 * {@link android.support.v7.app.ActionBarDrawerToggle.DrawerToggle#onDrawerClosed(View)}
 */
public interface NavigationDrawerListener {
    void onDrawerClosed(View view);

    void onDrawerOpened(View drawerView);
}
