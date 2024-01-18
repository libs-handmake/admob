package common.hoangdz.admob.ad_format.banner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdView
import common.hoangdz.admob.ad_format.AdFormatViewModel
import common.hoangdz.lib.jetpack_compose.exts.SafeModifier
import common.hoangdz.lib.jetpack_compose.exts.collectWhenResume
import common.hoangdz.lib.jetpack_compose.exts.shimmerEffect
import common.hoangdz.lib.jetpack_compose.navigation.LocalScreenConfigs
import common.hoangdz.lib.viewmodels.DataResult
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BannerView(adFormatViewModel: AdFormatViewModel = hiltViewModel()) {
    val owner = LocalLifecycleOwner.current
    val loaderStateCollection by adFormatViewModel.bannerLoaderState.collectWhenResume()
    if (loaderStateCollection.state != DataResult.DataState.ERROR) {
        val config = LocalScreenConfigs.current
        Box {
            AndroidView(modifier = SafeModifier.fillMaxWidth(), factory = {
                return@AndroidView AdView(it).also { view ->
                    adFormatViewModel.loadBanner(
                        config.route.replace("\\?.*".toRegex(), ""),
                        view,
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