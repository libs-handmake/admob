package common.hoangdz.admob.ad_format.banner

import android.content.Context
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import common.hoangdz.admob.config.AdState
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
        screenName: String,
        adView: AdView,
        useCollapsible: Boolean,
        owner: LifecycleOwner,
        adLoaderState: MutableStateFlow<DataResult<AdView>>,
        adListener: AdListener? = null
    ) {
        loadAD(screenName, adView, useCollapsible, adLoaderState, adListener)
    }

    private fun loadAD(
        screenName: String,
        adView: AdView,
        useCollapsible: Boolean,
        adLoaderState: MutableStateFlow<DataResult<AdView>>,
        adListener: AdListener? = null
    ) {
        if (!adView.adUnitId.isNullOrEmpty() || adView.isLoading) return
        if (premiumHolder.isPremium) {
            adLoaderState.value = DataResult(DataResult.DataState.ERROR)
            return
        }
        adLoaderState.value = DataResult(DataResult.DataState.LOADING)
        adView.adUnitId = adIds.bannerID
        adView.setAdSize(getAdSize(adView))
        adView.setOnPaidEventListener {
            AdState.onPaidEvent?.invoke(it, adView.responseInfo ?: return@setOnPaidEventListener)
        }
        adView.adListener = object : AdListener() {

            override fun onAdImpression() {
                Firebase.analytics.logEvent(
                    "banner_impression_$screenName", bundleOf()
                )
                adListener?.onAdImpression()
            }

            override fun onAdClicked() {
                Firebase.analytics.logEvent("banner_ad_clicked_$screenName", bundleOf())
                adListener?.onAdClicked()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adLoaderState.value =
                    if (premiumHolder.isPremium) DataResult(DataResult.DataState.ERROR) else DataResult(
                        DataResult.DataState.LOADED, adView
                    )
                adListener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                adLoaderState.value = DataResult(DataResult.DataState.ERROR)
                adListener?.onAdFailedToLoad(p0)
            }
        }
        val extras = bundleOf(
            "collapsible" to "bottom"
        ).takeIf { useCollapsible }
        val request = AdRequest.Builder().let {
            extras?.let { e -> it.addNetworkExtrasBundle(AdMobAdapter::class.java, e) } ?: it
        }.build()
        adView.loadAd(request)
    }

    private fun getAdSize(adView: AdView): AdSize {
        var adWidthPixels = adView.width * 1f
        if (adWidthPixels == 0f) {
            adWidthPixels = context.screenSize.first * 1f
        }
        val density: Float = context.resources.displayMetrics.density.takeIf { it > 0f } ?: 100f
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}