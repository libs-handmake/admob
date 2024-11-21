package common.hoangdz.admob.ad_format.native_ads.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.admob.ad_format.AdFormatViewModel
import common.hoangdz.lib.jetpack_compose.exts.SafeModifier
import common.hoangdz.lib.jetpack_compose.exts.collectWhenResume
import common.hoangdz.lib.viewmodels.DataResult
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NativeAdView(
    modifier: Modifier = SafeModifier,
    adViewModel: AdFormatViewModel,
    requestID: String,
    requireReload: Boolean = true,
    loading: (@Composable () -> Unit)? = null,
    onLoaded: (() -> Unit)? = null,
    androidView: (Context, nativeAD: StateFlow<DataResult<NativeAd>>, owner: LifecycleOwner) -> NativeAdAndroidView
) {
    if (!adViewModel.checkNativeAvailable(requestID)) return
    val adBinding = adViewModel.loadNativeAds(requestID, requireReload)
    val adStateCollector by adBinding.nativeAdState.collectWhenResume()
    val owner = LocalLifecycleOwner.current
    val premiumState by adViewModel.isPremium.collectWhenResume()
    if (adStateCollector.state == DataResult.DataState.ERROR || premiumState) {
        return
    }

    LaunchedEffect(key1 = owner) {
        adBinding.attachToLifecycle(owner)
    }

    LaunchedEffect(key1 = adStateCollector.state) {
        if (adStateCollector.state == DataResult.DataState.LOADED) {
            onLoaded?.invoke()
        }
    }
    Box(SafeModifier.fillMaxWidth()) {
        Box(modifier = modifier) {
            if (adStateCollector.state == DataResult.DataState.LOADED) AndroidView(modifier = SafeModifier.fillMaxWidth(),
                factory = {
                    androidView(it, adBinding.nativeAdState, owner)
                },
                update = {
                    it.bindAds(adStateCollector.value)
                })
            if (adStateCollector.state == DataResult.DataState.LOADING || adStateCollector.state == DataResult.DataState.IDLE) loading?.invoke()
        }
    }
}