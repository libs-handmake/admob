package common.hoangdz.admob.config.shared

import android.content.Context
import common.hoangdz.admob.config.shared.AdSharedSetting.MAX_NATIVE_AD_THRESHOLD
import common.hoangdz.lib.utils.PreferenceHelper

@Suppress("ClassName")
class AdShared_Impl(context: Context) : PreferenceHelper(context), AdShared {

    override fun getPrefName(): String {
        return "ad_setting_shared"
    }

    override var nativeLoaderThreshold: Int
        get() = pref.getInt(MAX_NATIVE_AD_THRESHOLD.first, MAX_NATIVE_AD_THRESHOLD.second)
        set(value) {
            return putInt(MAX_NATIVE_AD_THRESHOLD.first, value)
        }
}