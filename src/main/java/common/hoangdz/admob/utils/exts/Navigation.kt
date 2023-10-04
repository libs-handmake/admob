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
    route: String, activity: Activity?, replacement: Boolean = false
) {
    activity?.invokeWithInterstitial {
        if (replacement) navigateAndReplace(route)
        else navController.navigate(route)
    }
}

@Composable
fun ScreenConfigs.popNavigationWithAds() {
    val activity = LocalContext.current.getActivity() ?: return
    popNavigationWithAds(activity)
}

fun ScreenConfigs.popNavigationWithAds(
    activity: Activity?,
) {
    activity?.invokeWithInterstitial {
        navController.popBackStack()
    }
}