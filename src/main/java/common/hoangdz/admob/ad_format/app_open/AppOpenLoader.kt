package common.hoangdz.admob.ad_format.app_open

import android.app.Activity
import com.google.android.gms.ads.appopen.AppOpenAd
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.shared.AdShared
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenLoader @Inject constructor(private val adsShared: AdShared) :
    FullScreenAdsLoader<AppOpenAd>() {
    override fun onShow(
        activity: Activity?, availableAd: AppOpenAd, adLoaderListener: AdLoaderListener?
    ) {
        availableAd.show(activity ?: return)
    }

    override fun onLoad(adLoaderListener: AdLoaderListener?) {

    }
}