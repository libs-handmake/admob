package common.hoangdz.admob.ad_format.native_ads.loader

import com.google.android.gms.ads.nativead.NativeAd

class NativeAdHolder {
    private val savedNativeAds by lazy { mutableListOf<NativeAd>() }

    val availableNativeAd
        get() = synchronized(savedNativeAds) {
            savedNativeAds.size
        }

    fun appendNativeAd(vararg nativeAds: NativeAd) {
        synchronized(savedNativeAds) {
            savedNativeAds.addAll(nativeAds)
        }
    }

    fun getNativeAd(): NativeAd? {
        return synchronized(savedNativeAds) {
            return@synchronized savedNativeAds.removeFirstOrNull()
        }
    }

    fun release() {
        synchronized(savedNativeAds) {
            for (nativeAd in savedNativeAds) {
                nativeAd.destroy()
            }
            savedNativeAds.clear()
        }
    }
}