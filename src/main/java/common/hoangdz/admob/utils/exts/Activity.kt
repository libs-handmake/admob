package common.hoangdz.admob.utils.exts

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavOptions
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import common.hoangdz.admob.ad_format.full_screen_native_ads.view.screen.FullScreenNativeAdRoute
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject
import common.hoangdz.lib.extensions.getActivity
import common.hoangdz.lib.extensions.launchIO
import common.hoangdz.lib.extensions.launchWhen
import common.hoangdz.lib.jetpack_compose.exts.noAnimation
import common.hoangdz.lib.jetpack_compose.navigation.LocalScreenConfigs
import common.hoangdz.lib.jetpack_compose.navigation.ScreenConfigs
import common.hoangdz.lib.lifecycle.ActivityLifecycleManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay

@Composable
fun invokeWithInterstitial(onInterPassed: (Boolean) -> Unit) {
    LocalContext.current.getActivity()
        ?.invokeWithInterstitial(LocalScreenConfigs.current.route, null, onInterPassed)
        ?: onInterPassed(false)
}

fun Activity.invokeWithInterstitial(
    screenName: String, overrideId: String? = null, onInterPassed: (Boolean) -> Unit
) {
    if (this is AppCompatActivity) {
        val entryPoint = appInject<AdmobEntryPoint>()
        val interLoader = entryPoint.interstitialLoader()
        val adsShared = entryPoint.adsShared()
        val fullscreenLoader = entryPoint.fullscreenNativeLoader()
        interLoader.show(this, object : AdLoaderListener(overrideId) {

            override fun onAdStartShow() {
                if (adsShared.nativeFullScreenAfterInter && FullScreenNativeAdRoute.adContent != null) fullscreenLoader.loadNativeAdIfNeeded(
                    false
                )
            }

            override fun onAdClicked() {
                Firebase.analytics.logEvent("inter_clicked_$screenName", bundleOf())
            }

            override fun onInterPassed(showed: Boolean) {
                GlobalScope.launchIO {
                    var owner: LifecycleOwner? = null
                    var tryAgain = 0
                    while (ActivityLifecycleManager[this@invokeWithInterstitial]?.also {
                            owner = it
                        } == null) {
                        if (tryAgain == 3) return@launchIO
                        tryAgain++
                        delay(500)
                    }
                    owner?.launchWhen(
                        Lifecycle.State.RESUMED
                    ) {
                        if (fullscreenLoader.availableNativeAd > 0 && adsShared.nativeFullScreenAfterInter && FullScreenNativeAdRoute.adContent != null && showed) {
                            FullScreenNativeAdRoute.onComplete = {
                                onInterPassed(showed)
                            }
                            ScreenConfigs.navController?.navigate(
                                route = FullScreenNativeAdRoute.navigationInfo(),
                                navOptions = NavOptions.Builder().noAnimation().build(),
                                null
                            )
                        } else onInterPassed(showed)
                    }
                }
            }
        })
    } else onInterPassed(false)
}