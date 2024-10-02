package common.hoangdz.admob.ad_format.full_screen_native_ads.loader

import android.content.Context
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdOptions
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdHolder
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdKeeper
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdQueue
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.extensions.availableToLoad
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FullScreenNativeAdsLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adIds: AdIds,
    private val adShared: AdShared
) {

    private val nativeAdHolder by lazy { NativeAdHolder() }

    private val nativeAdQueue by lazy { mutableListOf<NativeAdQueue>() }

    private var loadingAds = 0

    val availableNativeAd get() = nativeAdHolder.availableNativeAd

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

    fun loadNativeAdIfNeeded(forceLoadAds: Boolean = false) {
        val needToLoad = adShared.nativeLoaderThreshold - availableNativeAd
        if ((loadingAds >= needToLoad || availableNativeAd >= needToLoad) && !forceLoadAds) return
        loadingAds++
        NativeAdKeeper(context, adIds.nativeFullScreen, {
            val videoOptions =
                VideoOptions.Builder().setStartMuted(false).setCustomControlsRequested(false)
                    .build()
            val adOptions = NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
                .setVideoOptions(videoOptions).build()
            withNativeAdOptions(adOptions)
        }, {
            distributeAds(true)
            loadingAds--
        }, {
            loadingAds--
            nativeAdHolder.appendNativeAd(it)
            distributeAds(false)
        }).loadAd()

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