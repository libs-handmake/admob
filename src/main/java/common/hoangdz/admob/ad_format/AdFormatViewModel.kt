package common.hoangdz.admob.ad_format

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.admob.ad_format.banner.BannerLoader
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdsLoader
import common.hoangdz.lib.extensions.launchIO
import common.hoangdz.lib.viewmodels.AppViewModel
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AdFormatViewModel @Inject constructor(
    application: Application,
    private val bannerLoader: BannerLoader,
    private val nativeAdsLoader: NativeAdsLoader
) : AppViewModel(application) {

    private val nativeAdMapper by lazy { hashMapOf<String, MutableStateFlow<DataResult<NativeAd>>>() }

    private val _bannerLoaderState by lazy {
        MutableStateFlow(DataResult<AdView>(DataResult.DataState.IDLE))
    }

    val bannerLoaderState by lazy { _bannerLoaderState.asStateFlow() }

    fun loadBanner(
        adView: AdView, owner: LifecycleOwner
    ) {
        bannerLoader.loadBannerAd(adView, owner, _bannerLoaderState)
    }

    fun loadNativeAds(requestId: String): MutableStateFlow<DataResult<NativeAd>> {
        val nativeLoaderState = synchronized(nativeAdMapper) {
            nativeAdMapper[requestId] ?: MutableStateFlow(
                DataResult<NativeAd>(
                    DataResult.DataState.IDLE
                )
            ).also { nativeAdMapper[requestId] = it }
        }
        viewModelScope.launchIO {
            nativeAdsLoader.enqueueNativeAds(nativeLoaderState)
        }
        return nativeLoaderState
    }

    override fun onCleared() {
        super.onCleared()
        synchronized(nativeAdMapper) {
            nativeAdMapper.entries.forEach {
                it.value.value.value?.destroy()
            }
            nativeAdMapper.clear()
        }
    }
}