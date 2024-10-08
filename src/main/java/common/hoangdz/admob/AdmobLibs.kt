package common.hoangdz.admob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.admob.utils.user_message_platform.UserConsentRequester
import common.hoangdz.lib.extensions.appInject
import common.hoangdz.lib.extensions.launchIO
import common.hoangdz.lib.extensions.launchMain
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

class AdmobLibs {
    companion object {
        var initialized = false
        private var needToCallInitialize = false

        fun initializeWithConsent(
            activity: Activity,
            remoteConfigDefault: Map<String, Any>?,
            initAppOpen: Boolean = true,
            onConsentRequestDismiss: (() -> Unit)? = null,
            onBiddingConsentApply: (() -> Unit)? = null,
            onRemoteFetched: (config: FirebaseRemoteConfig) -> Unit
        ) {
            UserConsentRequester.requestConsentInformation(activity, onConsentRequestDismiss) {
                initialize(
                    activity,
                    remoteConfigDefault,
                    onBiddingConsentApply,
                    initAppOpen,
                    onRemoteFetched
                )
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun initialize(
            context: Context,
            remoteConfigDefault: Map<String, Any>?,
            onBiddingConsentApply: (() -> Unit)? = null,
            initAppOpen: Boolean = true,
            onRemoteFetched: (config: FirebaseRemoteConfig) -> Unit
        ) {
            val admobEntryPoint = context.appInject<AdmobEntryPoint>()
            admobEntryPoint.adRemoteConfig().fetchRemoteConfig(remoteConfigDefault ?: mapOf()) {
                val consentInformation = UserMessagingPlatform.getConsentInformation(context)
                if (!consentInformation.canRequestAds()) {
                    onRemoteFetched(it)
                    return@fetchRemoteConfig
                }
                if (needToCallInitialize) {
                    onRemoteFetched(it)
                    return@fetchRemoteConfig
                }
                needToCallInitialize = true
                val configuration = RequestConfiguration.Builder().setTestDeviceIds(
                    listOf(
                        "812201C6E5F501E98EA1298F4A034968",
                        "D5A455C4AEA084A209D8A63ED9DE9483",
                        "AFEB93FB136E17F7319901033C81B8E5",
                        "5E9443F94E7A3271A01B7F72618F3CFB",
                        "8F156B99DFB1020FE53F27B325B91DF8",//Wavez Samsung android 14
                        "1B5E498583B753E34DAA54261F3EA4F5"// pixel 2 XL wavez
                    )
                ).build()
                onBiddingConsentApply?.invoke()
                MobileAds.setRequestConfiguration(configuration)
                GlobalScope.launchIO {
                    MobileAds.initialize(context) {
                        initialized = true
                        if (initAppOpen) {
                            GlobalScope.launchMain {
                                admobEntryPoint.appOpenLoader().load(null)
                            }
                        }
                    }
                }
                onRemoteFetched(it)
            }

        }
    }
}