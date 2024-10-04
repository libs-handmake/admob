package common.hoangdz.admob.ad_format.native_ads.loader

import android.app.Activity
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.extensions.availableToLoad
import common.hoangdz.lib.viewmodels.DataResult
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdsLoader @Inject constructor(
    private val adIds: AdIds, private val adShared: AdShared
) {

    var activity: WeakReference<Activity>? = null

    fun clearUnavailableNativeAds(activity: Activity) {
        this.activity?.clear()
        nativeAdHolder.clearUnAvailableNative(activity)
    }

    private val nativeAdHolder by lazy { NativeAdHolder() }

    private val nativeAdQueue by lazy { mutableListOf<NativeAdQueue>() }

    private var loadingAds = 0

    private fun distributeAds(responseErrorIfFailed: Boolean) = synchronized(nativeAdQueue) {
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
        loadNativeAdIfNeeded()
    }

    private fun loadNativeAdIfNeeded() {
        val needToLoad = adShared.nativeLoaderThreshold - nativeAdHolder.availableNativeAd
        if (needToLoad > loadingAds) {
            loadingAds++
            NativeAdKeeper(activity?.get() ?: kotlin.run {
                loadingAds--
                return
            }, adIds.nativeID, {}, {
                distributeAds(true)
                loadingAds--
            }, {
                loadingAds--
                nativeAdHolder.appendNativeAd(it)
                distributeAds(false)
            }).loadAd()
        }
    }


    fun removeQueue(id: String) = synchronized(nativeAdQueue) {
        nativeAdQueue.removeAll { it.queueID == id }
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