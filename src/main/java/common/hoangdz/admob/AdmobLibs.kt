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

class AdmobLibs {
    companion object {
        var initialized = false
        private var needToCallInitialize = false

        fun initializeWithConsent(
            activity: Activity,
            remoteConfigDefault: Map<String, Any>?,
            onConsentRequestDismiss: (() -> Unit)? = null,
            onBiddingConsentApply: (() -> Unit)? = null,
            onRemoteFetched: (config: FirebaseRemoteConfig) -> Unit
        ) {
            UserConsentRequester.requestConsentInformation(activity, onConsentRequestDismiss) {
                initialize(
                    activity, remoteConfigDefault, onBiddingConsentApply, onRemoteFetched
                )
            }
        }

        fun initialize(
            context: Context,
            remoteConfigDefault: Map<String, Any>?,
            onBiddingConsentApply: (() -> Unit)? = null,
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
                        "5E9443F94E7A3271A01B7F72618F3CFB"
                    )
                ).build()
                onBiddingConsentApply?.invoke()
                MobileAds.setRequestConfiguration(configuration)
                MobileAds.initialize(context) {
                    initialized = true
                    admobEntryPoint.appOpenLoader().load(null)
                }
                onRemoteFetched(it)
            }

        }
    }
}