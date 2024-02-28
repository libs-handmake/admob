package common.hoangdz.admob.ad_format.reward_ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.ad_id.AdIds
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardAdLoader @Inject constructor(
    @ApplicationContext private val context: Context, private val adIds: AdIds
) : FullScreenAdsLoader<RewardedAd>() {

    override fun onTimeShowSaved() {

    }

    override fun onLoad(activity: Activity?, adLoaderListener: AdLoaderListener?) {
        if (availableAd != null) return
        RewardedAd.load(context,
            adIds.rewardID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    onLoaded(p0)
                    show(activity, adLoaderListener)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    onFailedToLoad(adLoaderListener)
                }
            })
    }

    override fun onShow(
        activity: Activity?, availableAd: RewardedAd, adLoaderListener: AdLoaderListener?
    ) {
        availableAd.fullScreenContentCallback = getFullScreenContentCallback(adLoaderListener)
        availableAd.show(
            activity ?: return
        ) {
            adLoaderListener?.onAdReward()
        }
    }
}