package common.hoangdz.admob

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject

class AdmobLibs {
    companion object {
        var initialized = false
        fun initialize(context: Context) {
            val admobEntryPoint = context.appInject<AdmobEntryPoint>()
            admobEntryPoint.adRemoteConfig().fetchRemoteConfig()
            MobileAds.initialize(context) {
                initialized = true
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("812201C6E5F501E98EA1298F4A034968"))
                admobEntryPoint.appOpenLoader().load()
            }
        }
    }
}