package common.hoangdz.admob.ad_format.native_ads.loader

import android.content.Context
import androidx.core.os.bundleOf
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import common.hoangdz.admob.config.AdState

data class NativeAdKeeper(
    private val context: Context,
    private val adId: String,
    private val onFailedToLoad: (LoadAdError) -> Unit = {},
    private val onLoaded: (NativeAdKeeper) -> Unit = {}
) {

    var loaderID: String? = null

    var nativeAd: NativeAd? = null

    private val adLoader by lazy {
        AdLoader.Builder(context, adId).forNativeAd {
            it.setOnPaidEventListener { adValue ->
                AdState.onPaidEvent?.invoke(
                    adValue, it.responseInfo ?: return@setOnPaidEventListener
                )
            }
            nativeAd = it
            onLoaded(this)
        }.withAdListener(object : AdListener() {
            override fun onAdOpened() {
                super.onAdOpened()
                Firebase.analytics.logEvent("native_ad_clicked_$loaderID", bundleOf())
            }

            override fun onAdImpression() {
                Firebase.analytics.logEvent("native_ad_impression_$loaderID", bundleOf())
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                onFailedToLoad(p0)
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
    }

    fun loadAd() {
        adLoader.loadAds(
            AdManagerAdRequest.Builder().build(), 1
        )
    }
}