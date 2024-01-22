package common.hoangdz.admob.utils.exts

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject
import common.hoangdz.lib.extensions.getActivity
import common.hoangdz.lib.extensions.launchIO
import common.hoangdz.lib.extensions.launchWhen
import common.hoangdz.lib.jetpack_compose.navigation.LocalScreenConfigs
import common.hoangdz.lib.lifecycle.ActivityLifecycleManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay

@Composable
fun invokeWithInterstitial(onInterPassed: () -> Unit) {
    LocalContext.current.getActivity()
        ?.invokeWithInterstitial(LocalScreenConfigs.current.route, onInterPassed) ?: onInterPassed()
}

fun Activity.invokeWithInterstitial(screenName: String, onInterPassed: () -> Unit) {
    if (this is AppCompatActivity) {
        val interLoader = appInject<AdmobEntryPoint>().interstitialLoader()
        interLoader.show(this, object : AdLoaderListener() {
            override fun onAdClicked() {
                Firebase.analytics.logEvent("inter_clicked_$screenName", bundleOf())
            }

            override fun onInterPassed() {
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
                        onInterPassed()
                    }
                }
            }
        })
    } else onInterPassed()
}