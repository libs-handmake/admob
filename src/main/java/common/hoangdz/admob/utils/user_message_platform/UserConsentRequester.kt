package common.hoangdz.admob.utils.user_message_platform

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import common.hoangdz.lib.extensions.logError

class UserConsentRequester {
    companion object {
        fun requestConsentInformation(
            activity: Activity,
            onConsentRequestDismiss: (() -> Unit)?,
            onRequestInitAdSdk: () -> Unit
        ) {
            val debugSetting = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("5E9443F94E7A3271A01B7F72618F3CFB")
                .addTestDeviceHashedId("812201C6E5F501E98EA1298F4A034968")
                .build()

            val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSetting)
                .setTagForUnderAgeOfConsent(false).build()

            val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
            consentInformation.requestConsentInfoUpdate(activity, params, {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->
                    logError("${loadAndShowError?.message} : ${loadAndShowError?.errorCode}")
                    onRequestInitAdSdk()
                    onConsentRequestDismiss?.invoke()
                }
            }, { requestConsentError ->
                logError("request consent error ${requestConsentError.message} : ${requestConsentError.errorCode}")
                onConsentRequestDismiss?.invoke()
            })
            onRequestInitAdSdk()
        }
    }
}