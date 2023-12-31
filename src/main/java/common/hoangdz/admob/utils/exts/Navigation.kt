package common.hoangdz.admob.utils.exts

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import common.hoangdz.lib.extensions.getActivity
import common.hoangdz.lib.jetpack_compose.navigation.ScreenConfigs

@Composable
fun ScreenConfigs.navigateWithAds(route: String, replacement: Boolean) {
    val activity = LocalContext.current.getActivity() ?: return
    navigateWithAds(route, activity, replacement)
}

fun ScreenConfigs.navigateWithAds(
    route: String, activity: Activity?, replacement: Boolean = false, ignoredAds: Boolean = false
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
    activity?.invokeWithInterstitial {
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
    activity: Activity?, navID: String? = null
) {
    activity?.invokeWithInterstitial {
        if (navID.isNullOrEmpty()) ScreenConfigs.navController?.popBackStack()
        else ScreenConfigs.navController?.popBackStack(navID, true)
    }
}