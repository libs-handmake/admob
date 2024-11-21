package common.hoangdz.admob.ad_format

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.admob.ad_format.banner.BannerLoader
import common.hoangdz.admob.ad_format.full_screen_native_ads.loader.FullScreenNativeAdsLoader
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdQueue
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdsLoader
import common.hoangdz.admob.ad_format.native_ads.model.NativeAdBinding
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.jetpack_compose.exts.compareAndSet
import common.hoangdz.lib.utils.user.PremiumHolder
import common.hoangdz.lib.viewmodels.AppViewModel
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AdFormatViewModel @Inject constructor(
    application: Application,
    private val bannerLoader: BannerLoader,
    private val nativeAdsLoader: NativeAdsLoader,
    private val fullScreenNativeAdsLoader: FullScreenNativeAdsLoader,
    private val premiumHolder: PremiumHolder,
    private val adShared: AdShared
) : AppViewModel(application) {

    private val nativeConfig by lazy { adShared.nativeAdConfig }

    val isPremium get() = premiumHolder.premiumState

    val isPremiumValue get() = premiumHolder.isPremium

    private val bannerConfig by lazy { adShared.bannerScreenConfigs }

    private val nativeAdMapper by lazy { hashMapOf<String, NativeAdBinding>() }
    private val fullScreenNativeAdMapper by lazy { hashMapOf<String, MutableStateFlow<DataResult<NativeAd>>>() }

    private val _bannerLoaderState by lazy {
        MutableStateFlow(DataResult<AdView>(DataResult.DataState.IDLE))
    }

    val bannerLoaderState by lazy { _bannerLoaderState.asStateFlow() }

    private var reloadNativeJob: Job? = null

    private val _bannerReloadRequester by lazy { MutableStateFlow(0) }
    val bannerReloadRequester by lazy { _bannerReloadRequester.asStateFlow() }

    fun requestReloadBanner() {
        _bannerReloadRequester.compareAndSet(1 - _bannerReloadRequester.value)
    }

    fun bannerConfigOf(id: String) = bannerConfig[id]

    fun checkNativeAvailable(id: String) = nativeConfig[id] ?: true

    fun loadBanner(
        screenName: String,
        adView: AdView,
        useCollapsible: Boolean,
        owner: LifecycleOwner,
        adListener: AdListener? = null
    ) {
        bannerLoader.loadBannerAd(
            screenName, adView, useCollapsible, owner, _bannerLoaderState, adListener
        )
    }


    fun loadNativeAds(
        requestId: String, reload: Boolean = false
    ): NativeAdBinding {
        val nativeLoaderState = synchronized(nativeAdMapper) {
            val state = nativeAdMapper[requestId] ?: NativeAdBinding(
                context, requestId, viewModelScope, reload
            ).also {
                nativeAdMapper[requestId] = it
            }
            state
        }
        return nativeLoaderState
    }

    fun loadFullScreenNativeAds(requestId: String): MutableStateFlow<DataResult<NativeAd>> {
        val nativeLoaderState = synchronized(fullScreenNativeAdMapper) {
            val state = fullScreenNativeAdMapper[requestId] ?: MutableStateFlow(
                DataResult<NativeAd>(
                    DataResult.DataState.IDLE
                )
            ).also { fullScreenNativeAdMapper[requestId] = it }
            fullScreenNativeAdsLoader.enqueueNativeAds(NativeAdQueue(requestId, state))
            state
        }
        return nativeLoaderState
    }

    override fun onCleared() {
        super.onCleared()
        synchronized(nativeAdMapper) {
            nativeAdMapper.entries.forEach {
                it.value.destroy()
            }
            nativeAdMapper.clear()
        }
        synchronized(fullScreenNativeAdMapper) {
            fullScreenNativeAdMapper.entries.forEach {
                it.value.value.value?.destroy()
                fullScreenNativeAdsLoader.removeQueue(it.key)
            }
            fullScreenNativeAdMapper.clear()
        }
    }
}