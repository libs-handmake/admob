package common.hoangdz.admob.ad_format.native_ads.loader

import android.app.Activity
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdHolder {
    private val savedNativeAds by lazy { mutableListOf<NativeAdKeeper>() }

    val availableNativeAd
        get() = synchronized(savedNativeAds) {
            savedNativeAds.size
        }

    fun appendNativeAd(vararg adKeepers: NativeAdKeeper) {
        synchronized(savedNativeAds) {
            savedNativeAds.addAll(adKeepers)
        }
    }

    fun getNativeAd(loaderId: String): NativeAd? {
        return synchronized(savedNativeAds) {
            return@synchronized savedNativeAds.removeFirstOrNull()
                ?.also { it.loaderID = loaderId }?.nativeAd
        }
    }

    fun release() {
        synchronized(savedNativeAds) {
            for (nativeAd in savedNativeAds) {
                nativeAd.nativeAd?.destroy()
            }
            savedNativeAds.clear()
        }
    }

    fun clearUnAvailableNative(activity: Activity) {
        synchronized(savedNativeAds) {
            savedNativeAds.removeAll {
                it.nativeAd?.destroy()
                it.context == activity
            }
        }
    }
}