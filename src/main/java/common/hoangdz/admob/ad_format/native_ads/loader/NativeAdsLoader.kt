package common.hoangdz.admob.ad_format.native_ads.loader

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.extensions.availableToLoad
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdsLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adIds: AdIds,
    private val adShared: AdShared
) {

    private val nativeAdHolder by lazy { NativeAdHolder() }

    private val nativeAdQueue by lazy { mutableListOf<MutableStateFlow<DataResult<NativeAd>>>() }

    private val adLoader by lazy {
        AdLoader.Builder(context, adIds.nativeID).forNativeAd {
            nativeAdHolder.appendNativeAd(it)
            distributeAds(false)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                distributeAds(true)
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
    }

    private fun distributeAds(responseErrorIfFailed: Boolean) = synchronized(nativeAdQueue) {
        synchronized(nativeAdQueue) {
            while (nativeAdQueue.isNotEmpty()) {
                val queue = nativeAdQueue.firstOrNull() ?: break
                val nativeAd = nativeAdHolder.getNativeAd()
                queue.value = if (nativeAd != null) DataResult(
                    DataResult.DataState.LOADED, nativeAd
                ) else if (responseErrorIfFailed) DataResult(DataResult.DataState.ERROR) else break
                nativeAdQueue.removeFirst()
            }
        }
        loadNativeAdIfNeeded()
    }

    private fun loadNativeAdIfNeeded() {
        val needToLoad = adShared.nativeLoaderThreshold - nativeAdHolder.availableNativeAd
        if (needToLoad > 0 && !adLoader.isLoading) {
            adLoader.loadAds(
                AdManagerAdRequest.Builder().build(), needToLoad
            )
        }
    }

    fun enqueueNativeAds(queue: MutableStateFlow<DataResult<NativeAd>>) {
        synchronized(nativeAdQueue) {
            if (!queue.value.state.availableToLoad(false)) return
            queue.value = DataResult(DataResult.DataState.LOADING)
            nativeAdQueue.add(queue)
        }
        distributeAds(false)
    }
}