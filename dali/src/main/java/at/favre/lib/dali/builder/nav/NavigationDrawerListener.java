package at.favre.lib.dali.builder.nav;

import android.view.View;

/**
 * Same as {@link androidx.appcompat.app.ActionBarDrawerToggle.DrawerToggle#onDrawerOpened(View)} and
 * {@link androidx.appcompat.app.ActionBarDrawerToggle.DrawerToggle#onDrawerClosed(View)}
 */
public interface NavigationDrawerListener {
    void onDrawerClosed(View view);

    void onDrawerOpened(View drawerView);
}
