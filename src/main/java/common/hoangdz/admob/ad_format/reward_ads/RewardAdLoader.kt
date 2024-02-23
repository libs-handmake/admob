package common.hoangdz.admob.ad_format.reward_ads

import android.app.Activity
import com.google.android.gms.ads.rewarded.RewardedAd
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener

class RewardAdLoader : FullScreenAdsLoader<RewardedAd>() {
    override fun onTimeShowSaved() {

    }

    override fun onLoad(adLoaderListener: AdLoaderListener?) {
    }

    override fun onShow(
        activity: Activity?, availableAd: RewardedAd, adLoaderListener: AdLoaderListener?
    ) {
        adLoaderListener?.onInterPassed()
    }
}