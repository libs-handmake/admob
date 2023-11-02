package common.hoangdz.admob

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject

class AdmobLibs {
    companion object {
        var initialized = false
        fun initialize(context: Context, onRemoteFetched: () -> Unit) {
            val admobEntryPoint = context.appInject<AdmobEntryPoint>()
            admobEntryPoint.adRemoteConfig().fetchRemoteConfig(onRemoteFetched)
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "812201C6E5F501E98EA1298F4A034968",
                    "D5A455C4AEA084A209D8A63ED9DE9483",
//                    "AFEB93FB136E17F7319901033C81B8E5"
                )
            ).build()
            MobileAds.setRequestConfiguration(configuration)
            MobileAds.initialize(context) {
                initialized = true
                admobEntryPoint.appOpenLoader().load()
            }
        }
    }
}