package common.hoangdz.admob.utils.exts

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject
import common.hoangdz.lib.extensions.getActivity
import common.hoangdz.lib.jetpack_compose.navigation.ScreenConfigs
import common.hoangdz.lib.utils.ads.GlobalAdState

@Composable
fun ScreenConfigs.navigateWithAds(route: String, replacement: Boolean) {
    val activity = LocalContext.current.getActivity() ?: return
    navigateWithAds(route, activity, null, replacement)
}

fun ScreenConfigs.navigateWithAds(
    route: String,
    activity: Activity?,
    overrideId: String? = null,
    replacement: Boolean = false,
    ignoredAds: Boolean = false
) {
    fun navigate() {
        kotlin.runCatching {
            if (replacement) navigateAndReplace(route)
            else ScreenConfigs.navController?.navigate(route)
        }
    }
    if (ignoredAds) {
        navigate()
        return
    }
    activity?.invokeWithInterstitial(
        this.route.replace("\\?.*".toRegex(), ""), overrideId = overrideId
    ) {
        GlobalAdState.isShowInterForNavigationLastTime = it
        navigate()
    }
}

@Composable
fun ScreenConfigs.popNavigationWithAds(
    navID: String? = null,
) {
    val activity = LocalContext.current.getActivity() ?: return
    popNavigationWithAds(activity, navID)
}

fun ScreenConfigs.popNavigationWithAds(
    activity: Activity?, navID: String? = null, ignoredAds: Boolean? = null
) {
    val ignoredAdsP =
        ignoredAds ?: (activity?.appInject<AdmobEntryPoint>()?.adsShared()?.useInterOnBack == false)

    fun pop() {
        if (navID.isNullOrEmpty()) ScreenConfigs.navController?.popBackStack()
        else ScreenConfigs.navController?.popBackStack(navID, true)
    }
    if (ignoredAdsP) {
        pop()
        return
    }
    activity?.invokeWithInterstitial("pop_${this.route.replace("\\?.*".toRegex(), "")}") {
        pop()
    }
}