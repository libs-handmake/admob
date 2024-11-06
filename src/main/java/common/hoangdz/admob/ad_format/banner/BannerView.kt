package common.hoangdz.admob.ad_format.banner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdView
import common.hoangdz.admob.ad_format.AdFormatViewModel
import common.hoangdz.lib.jetpack_compose.exts.SafeModifier
import common.hoangdz.lib.jetpack_compose.exts.collectWhenResume
import common.hoangdz.lib.jetpack_compose.exts.shimmerEffect
import common.hoangdz.lib.jetpack_compose.navigation.LocalScreenConfigs
import common.hoangdz.lib.viewmodels.DataResult
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BannerView(
    usingCollapsible: Boolean = true,
    id: String = LocalScreenConfigs.current.actualRouteName,
    adFormatViewModel: AdFormatViewModel = hiltViewModel()
) {
    val owner = LocalLifecycleOwner.current
    val loaderStateCollection by adFormatViewModel.bannerLoaderState.collectWhenResume()
    if (loaderStateCollection.state != DataResult.DataState.ERROR && adFormatViewModel.bannerConfigOf(
            id
        )?.showBanner != false
    ) {
        var adView by remember {
            mutableStateOf<AdView?>(null)
        }

        val config = LocalScreenConfigs.current

        DisposableEffect(key1 = owner) {
            val observer = LifecycleEventObserver { _, event ->
//            logError("state AD $event ${config.actualRouteName}")
                if (event == Lifecycle.Event.ON_RESUME) {
                    adView?.resume()
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    adView?.pause()
                }
            }
            owner.lifecycle.addObserver(observer)
            onDispose {
                adView?.destroy()
                owner.lifecycle.removeObserver(observer)
            }
        }
        Box {
            AndroidView(modifier = SafeModifier.fillMaxWidth(), factory = {
                return@AndroidView AdView(it).also { view ->
                    adView = view
                    adFormatViewModel.loadBanner(
                        config.actualRouteName,
                        view,
                        usingCollapsible && adFormatViewModel.bannerConfigOf(id)?.useCollapsed != false,
                        owner
                    )
                }
            })
            if (loaderStateCollection.state == DataResult.DataState.LOADING) {
                Row {
                    Box(
                        modifier = SafeModifier
                            .size(50.sdp)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = SafeModifier
                            .weight(1f)
                            .height(50.sdp)
                            .padding(start = 8.sdp)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}