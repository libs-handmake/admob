package common.hoangdz.admob

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdsLoader
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.components.ComposeActivity
import common.hoangdz.lib.extensions.appInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

object AdmobLibApp {

    fun startInit(app: Application) {
        app.registerActivityLifecycleCallbacks(
            AdmobLifecycleCallback(
                app.appInject<AdmobEntryPoint>().nativeAdsLoader()
            )
        )
    }

    internal fun updateActivity(activity: Activity?) {
        _currentActivity.value = activity
    }

    private val _currentActivity by lazy { MutableStateFlow<Activity?>(null) }
    val currentActivity by lazy { _currentActivity.asStateFlow() }
}

private class AdmobLifecycleCallback(private val nativeAdsLoader: NativeAdsLoader) :
    ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity !is ComposeActivity) return
        nativeAdsLoader.activity = WeakReference(activity)
        AdmobLibApp.updateActivity(activity)

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity !is ComposeActivity) return
        nativeAdsLoader.activity = WeakReference(activity)
        AdmobLibApp.updateActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity !is ComposeActivity) return
        nativeAdsLoader.clearUnavailableNativeAds(activity)
        AdmobLibApp.updateActivity(null)
    }
}