package common.hoangdz.admob.ad_format.banner

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.lib.extensions.screenSize
import common.hoangdz.lib.utils.user.PremiumHolder
import common.hoangdz.lib.viewmodels.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adIds: AdIds,
    private val premiumHolder: PremiumHolder
) {
    fun loadBannerAd(
        adView: AdView, owner: LifecycleOwner, adLoaderState: MutableStateFlow<DataResult<AdView>>
    ) {
        loadAD(adView, adLoaderState)
    }

    private fun loadAD(
        adView: AdView, adLoaderState: MutableStateFlow<DataResult<AdView>>
    ) {
        if (!adView.adUnitId.isNullOrEmpty() || adView.isLoading) return
        if (premiumHolder.isPremium) {
            adLoaderState.value = DataResult(DataResult.DataState.ERROR)
            return
        }
        adLoaderState.value = DataResult(DataResult.DataState.LOADING)
        adView.adUnitId = adIds.bannerID
        adView.setAdSize(getAdSize(adView))
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adLoaderState.value =
                    if (premiumHolder.isPremium) DataResult(DataResult.DataState.ERROR) else DataResult(
                        DataResult.DataState.LOADED, adView
                    )
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                adLoaderState.value = DataResult(DataResult.DataState.ERROR)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun getAdSize(adView: AdView): AdSize {
        var adWidthPixels = adView.width * 1f
        if (adWidthPixels == 0f) {
            adWidthPixels = context.screenSize.first * 1f
        }
        val density: Float = context.resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}