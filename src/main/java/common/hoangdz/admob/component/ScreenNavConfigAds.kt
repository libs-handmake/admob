package common.hoangdz.admob.component

import android.app.Activity
import common.hoangdz.admob.utils.exts.popNavigationWithAds
import common.hoangdz.lib.jetpack_compose.navigation.ScreenConfigs
import common.hoangdz.lib.jetpack_compose.navigation.ScreenNavConfig

abstract class ScreenNavConfigAds<T> : ScreenNavConfig<T>() {

    override fun onBackPressed(activity: Activity?, configs: ScreenConfigs): Boolean {
        if (!super.onBackPressed(activity, configs)) configs.popNavigationWithAds(
            activity ?: return true
        )
        return true
    }
}