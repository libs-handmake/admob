package common.hoangdz.admob.ad_format.app_open

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import common.hoangdz.admob.AdmobLibs
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.AdState
import common.hoangdz.admob.config.ad_id.AdIds
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.utils.user.PremiumHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adIds: AdIds,
    private val adsShared: AdShared,
    private val premiumHolder: PremiumHolder
) : FullScreenAdsLoader<AppOpenAd>(), Application.ActivityLifecycleCallbacks,
    LifecycleEventObserver {

    private var currentActivity: Activity? = null

    companion object {
        var disableToShow = false
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun register(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun onShow(
        activity: Activity?, availableAd: AppOpenAd, adLoaderListener: AdLoaderListener?
    ) {
        availableAd.fullScreenContentCallback = getFullScreenContentCallback(adLoaderListener)
        availableAd.show(activity ?: return)
    }

    override fun onLoad(adLoaderListener: AdLoaderListener?) {
        AppOpenAd.load(context,
            adIds.appOpenID,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    onLoaded(p0)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    onFailedToLoad(adLoaderListener)
                }
            })
    }

    override fun onLoaded(ad: AppOpenAd) {
        super.onLoaded(ad)
        ad.setOnPaidEventListener {
            AdState.onPaidEvent?.invoke(it, ad.responseInfo)
        }
    }

    override fun onTimeShowSaved() {
        AdState.lastTimeShowAppOpenAds = System.currentTimeMillis()
    }

    override fun show(activity: Activity?, adLoaderListener: AdLoaderListener?): Boolean {
        if (premiumHolder.isPremium || !AdmobLibs.initialized || disableToShow) {
            adLoaderListener?.onInterPassed()
            return true
        }
        if (!adsShared.canShowAppOpen) return false
        return super.show(activity, adLoaderListener)
    }

    override fun onLoadNextAds(overrideId: String?) {
        load(object : AdLoaderListener(overrideId) {})
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            show(currentActivity)
            disableToShow = false
        }
    }
}