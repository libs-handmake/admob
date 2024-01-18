package common.hoangdz.admob.ad_format.native_ads.loader

import android.content.Context
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.extensions.availableToLoad
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdsLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adIds: AdIds,
    private val adShared: AdShared
) {

    private val nativeAdHolder by lazy { NativeAdHolder() }

    private val nativeAdQueue by lazy { mutableListOf<NativeAdQueue>() }

    private var loadingAds = 0

    private fun distributeAds(responseErrorIfFailed: Boolean) = synchronized(nativeAdQueue) {
        synchronized(nativeAdQueue) {
            while (nativeAdQueue.isNotEmpty()) {
                val queue = nativeAdQueue.firstOrNull() ?: break
                val nativeAd = nativeAdHolder.getNativeAd(queue.queueID)
                queue.adState.value = if (nativeAd != null) {
                    DataResult(
                        DataResult.DataState.LOADED, nativeAd
                    )
                } else if (responseErrorIfFailed) DataResult(DataResult.DataState.ERROR) else break
                nativeAdQueue.removeFirst()
            }
        }
        loadNativeAdIfNeeded()
    }

    private fun loadNativeAdIfNeeded() {
        val needToLoad = adShared.nativeLoaderThreshold - nativeAdHolder.availableNativeAd
        if (needToLoad > loadingAds) {
            loadingAds++
            NativeAdKeeper(context, adIds.nativeID, {
                distributeAds(true)
                loadingAds--
            }, {
                loadingAds--
                nativeAdHolder.appendNativeAd(it)
                distributeAds(false)
            }).loadAd()
        }
    }

    fun enqueueNativeAds(queue: NativeAdQueue) {
        synchronized(nativeAdQueue) {
            if (!queue.adState.value.state.availableToLoad(false)) return
            queue.adState.value = DataResult(DataResult.DataState.LOADING)
            nativeAdQueue.add(queue)
        }
        distributeAds(false)
    }
}