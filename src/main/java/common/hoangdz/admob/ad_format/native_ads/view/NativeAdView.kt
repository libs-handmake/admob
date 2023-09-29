package common.hoangdz.admob.ad_format.native_ads.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.admob.ad_format.AdFormatViewModel
import common.hoangdz.lib.extensions.logError
import common.hoangdz.lib.jetpack_compose.exts.collectWhenResume
import common.hoangdz.lib.viewmodels.DataResult
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    adViewModel: AdFormatViewModel,
    requestID: String,
    loading: (@Composable () -> Unit)? = null,
    androidView: (Context, nativeAD: StateFlow<DataResult<NativeAd>>, owner: LifecycleOwner) -> NativeAdAndroidView
) {
    val adState = adViewModel.loadNativeAds(requestID)
    val adStateCollector by adState.collectWhenResume()
    logError(adState)
    logError(adStateCollector.state)
    val owner = LocalLifecycleOwner.current
    if (adStateCollector.state == DataResult.DataState.ERROR) return
    Box(Modifier.fillMaxWidth()) {
        Box(modifier = modifier) {
            if (adStateCollector.state == DataResult.DataState.LOADED) AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = {
                    androidView(it, adState, owner)
                },
                update = {
                    it.bindAds(adStateCollector.value)
                })
            if (adStateCollector.state == DataResult.DataState.LOADING) loading?.invoke()
        }
    }
}