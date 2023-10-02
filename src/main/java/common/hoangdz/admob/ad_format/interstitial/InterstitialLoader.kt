package common.hoangdz.admob.ad_format.interstitial

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import common.hoangdz.admob.AdmobLibs
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.admob.config.water_flow.WaterFlowManager
import common.hoangdz.lib.extensions.logError
import common.hoangdz.lib.utils.user.PremiumHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adShared: AdShared,
    private val premiumHolder: PremiumHolder,
    private val adIds: AdIds
) : FullScreenAdsLoader<InterstitialAd>() {

    private val flow by lazy {
        WaterFlowManager(
            context, adIds.run {
                listOf(interHighFloorID, interMediumFloorId, interAllPriceFloorId)
            }, adIds.interstitialID
        )
    }

    override fun onLoaded(ad: InterstitialAd) {
        super.onLoaded(ad)
        flow.reset()
    }

    override fun onFailedToLoad(adLoaderListener: AdLoaderListener?) {
        super.onFailedToLoad(adLoaderListener)
        if (flow.canNext) {
            flow.next()
            load(adLoaderListener)
        } else {
            flow.failed()
        }
    }

    override fun show(activity: Activity?, adLoaderListener: AdLoaderListener?): Boolean {
        if (premiumHolder.isPremium || !AdmobLibs.initialized) {
            adLoaderListener?.onInterPassed()
            logError("failed 1")
            return true
        }
        if (!adShared.canShowInterstitial || !flow.validToRequestAds) {
            adLoaderListener?.onInterPassed()
            logError("failed 2")
            return false
        }
        logError("Load 3")
        MobileAds.setAppMuted(true)
        return super.show(activity, adLoaderListener)
    }

    override fun onShow(
        activity: Activity?, availableAd: InterstitialAd, adLoaderListener: AdLoaderListener?
    ) {
        if (activity == null) {
            adLoaderListener?.onInterPassed()
            return
        }
        availableAd.fullScreenContentCallback = getFullScreenContentCallback(adLoaderListener)
        availableAd.show(activity)
    }

    override fun onLoad(adLoaderListener: AdLoaderListener?) {
        InterstitialAd.load(context,
            flow.currentId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    onFailedToLoad(adLoaderListener)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    onLoaded(ad)
                }
            })
    }

    override fun onLoadNextAds() {
        load()
    }

}