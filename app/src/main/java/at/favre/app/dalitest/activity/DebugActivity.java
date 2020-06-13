package at.favre.app.dalitest.activity;

import androidx.annotation.NonNull;

import at.favre.app.dalitest.BuildConfig;
import at.favre.lib.hood.extended.PopHoodActivity;
import at.favre.lib.hood.interfaces.Config;
import at.favre.lib.hood.interfaces.Page;
import at.favre.lib.hood.interfaces.Pages;
import at.favre.lib.hood.util.defaults.DefaultProperties;

public class DebugActivity extends PopHoodActivity {
    @NonNull
    @Override
    public Pages getPageData(@NonNull Pages emptyPages) {
        Page page = emptyPages.addNewPage();
//        page.add(DefaultProperties.createSectionSourceControlAndCI(BuildConfig.GIT_REV, BuildConfig.GIT_BRANCH, BuildConfig.GIT_DATE, BuildConfig.BUILD_NUMBER, null, BuildConfig.BUILD_DATE));
        page.add(DefaultProperties.createSectionBasicDeviceInfo());
        page.add(DefaultProperties.createDetailedDeviceInfo(this));
        page.add(DefaultProperties.createSectionAppVersionInfoFromBuildConfig(BuildConfig.class));
        page.add(DefaultProperties.createSectionAppVersionInfoFromBuildConfig(at.favre.lib.dali.BuildConfig.class).removeHeader());

        return emptyPages;
    }

    @NonNull
    @Override
    public Config getConfig() {
        return Config.newBuilder().setShowZebra(false).setLogTag("DebugActivity").build();
    }
}
