package common.hoangdz.admob.ad_format

import android.app.Activity
import androidx.annotation.CallSuper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import common.hoangdz.admob.AdmobLibs
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.admob.config.AdState
import common.hoangdz.lib.viewmodels.DataResult

abstract class FullScreenAdsLoader<AD> {
    protected open val needToLoadOnInit = false

    private var busy = false

    protected var loaderState = DataResult.DataState.IDLE

    companion object {
        var showing = false
    }

    init {
        if (needToLoadOnInit) load()
    }

    var availableAd: AD? = null

    fun getFullScreenContentCallback(
        adLoaderListener: AdLoaderListener? = null
    ) = object : FullScreenContentCallback() {
        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            showing = false
            availableAd = null
            loaderState = DataResult.DataState.ERROR
            adLoaderListener?.onInterPassed()
            adLoaderListener?.onAdFailedToShow()
            onLoadNextAds()
            AdState.lastTimeShowInterAds = System.currentTimeMillis()
        }

        override fun onAdDismissedFullScreenContent() {
            showing = false
            loaderState = DataResult.DataState.IDLE
            adLoaderListener?.onAdClosed()
            adLoaderListener?.onInterPassed()
            availableAd = null
            onLoadNextAds()
            AdState.lastTimeShowInterAds = System.currentTimeMillis()
        }

        override fun onAdShowedFullScreenContent() {
            adLoaderListener?.onAdStartShow()
        }
    }

    protected abstract fun onShow(
        activity: Activity?,
        availableAd: AD,
        adLoaderListener: AdLoaderListener?
    )

    open fun show(activity: Activity?, adLoaderListener: AdLoaderListener? = null): Boolean {
        val ad = availableAd
        if (ad == null) {
            load(adLoaderListener)
            adLoaderListener?.onInterPassed()
            return false
        }
        try {
            showing = true
            onShow(activity, ad, adLoaderListener)
        } catch (e: Throwable) {
            showing = false
            availableAd = null
            adLoaderListener?.onInterPassed()
        }
        return true
    }

    protected open fun onLoadNextAds() {
    }

    fun load(adLoaderListener: AdLoaderListener? = null) {
        if (busy || !AdmobLibs.initialized) return
        busy = true
        availableAd = null
        onLoad(adLoaderListener)
    }

    @CallSuper
    protected open fun onLoaded(ad: AD) {
        availableAd = ad
        busy = false
    }

    @CallSuper
    protected open fun onFailedToLoad(adLoaderListener: AdLoaderListener? = null) {
        busy = false
        adLoaderListener?.onAdFailedToLoad()
    }

    protected abstract fun onLoad(adLoaderListener: AdLoaderListener?)

}