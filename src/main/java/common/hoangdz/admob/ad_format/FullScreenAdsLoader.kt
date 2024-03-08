package common.hoangdz.admob.ad_format

import android.app.Activity
import androidx.annotation.CallSuper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import common.hoangdz.admob.AdmobLibs
import common.hoangdz.admob.ad_format.listener.AdLoaderListener
import common.hoangdz.lib.viewmodels.DataResult

abstract class FullScreenAdsLoader<AD> {
    protected open val needToLoadOnInit = false

    private var busy = false

    protected var loaderState = DataResult.DataState.IDLE

    companion object {
        var showing = false
    }

    init {
        if (needToLoadOnInit) load(null)
    }

    var availableAd: AD? = null

    abstract fun onTimeShowSaved()

    fun getFullScreenContentCallback(
        adLoaderListener: AdLoaderListener? = null
    ) = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
            adLoaderListener?.onAdClicked()
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            showing = false
            availableAd = null
            loaderState = DataResult.DataState.ERROR
            adLoaderListener?.onInterPassed(false)
            adLoaderListener?.onAdFailedToShow()
            onLoadNextAds(overrideId = adLoaderListener?.overrideId)
            onTimeShowSaved()
        }

        override fun onAdDismissedFullScreenContent() {
            showing = false
            loaderState = DataResult.DataState.IDLE
            adLoaderListener?.onAdClosed()
            availableAd = null
            onLoadNextAds(overrideId = adLoaderListener?.overrideId)
            onTimeShowSaved()
        }

        override fun onAdShowedFullScreenContent() {
            onTimeShowSaved()
            adLoaderListener?.onAdStartShow()
            adLoaderListener?.onInterPassed(true)
        }
    }

    protected abstract fun onShow(
        activity: Activity?, availableAd: AD, adLoaderListener: AdLoaderListener?
    )

    open fun show(activity: Activity?, adLoaderListener: AdLoaderListener? = null): Boolean {
        val ad = availableAd
        if (ad == null) {
            load(activity, adLoaderListener)
            adLoaderListener?.onInterPassed(false)
            return false
        }
        try {
            showing = true
            onShow(activity, ad, adLoaderListener)
        } catch (e: Throwable) {
            showing = false
            availableAd = null
            adLoaderListener?.onInterPassed(false)
        }
        return true
    }

    protected open fun onLoadNextAds(overrideId: String?) {
    }

    fun load(activity: Activity?, adLoaderListener: AdLoaderListener? = null) {
        if (busy || !AdmobLibs.initialized) return
        busy = true
        availableAd = null
        onLoad(activity, adLoaderListener)
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

    protected abstract fun onLoad(activity: Activity?, adLoaderListener: AdLoaderListener?)

}