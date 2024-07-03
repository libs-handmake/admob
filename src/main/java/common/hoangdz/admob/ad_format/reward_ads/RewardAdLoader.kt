package common.hoangdz.admob.ad_format.reward_ads

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.lib.utils.ads.GlobalAdState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardAdLoader @Inject constructor(
    @ApplicationContext private val context: Context, private val adIds: AdIds
) : FullScreenAdsLoader<RewardedAd>() {

    var showOnLoaded = false

    private var timer: CountDownTimer? = null

    override fun onTimeShowSaved() {

    }

    override fun show(activity: Activity?, adLoaderListener: AdLoaderListener?): Boolean {
        showOnLoaded = true
        return super.show(activity, adLoaderListener)
    }

    override fun onLoad(activity: Activity?, adLoaderListener: AdLoaderListener?) {
        if (availableAd != null) return
        timer?.cancel()
        timer = object : CountDownTimer(
            GlobalAdState.REWARD_LOADER_TIMEOUT, GlobalAdState.REWARD_LOADER_TIMEOUT
        ) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                adLoaderListener?.onAdFailedToLoad()
                showOnLoaded = false
            }
        }.start()

        RewardedAd.load(context,
            adIds.rewardID,
            AdRequest.Builder().setHttpTimeoutMillis(20_000).build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    onLoaded(p0)
                    if (showOnLoaded) show(activity, adLoaderListener)
                    timer?.cancel()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    onFailedToLoad(adLoaderListener)
                    timer?.cancel()
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