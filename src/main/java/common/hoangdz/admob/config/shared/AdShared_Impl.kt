package common.hoangdz.admob.config.shared

import android.content.Context
import common.hoangdz.admob.BuildConfig
import common.hoangdz.admob.ad_format.FullScreenAdsLoader
import common.hoangdz.admob.config.AdState
import common.hoangdz.admob.config.shared.AdSharedSetting.APP_OPEN_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.FULL_SCREEN_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.IGNORED_GAP_THRESHOLD
import common.hoangdz.admob.config.shared.AdSharedSetting.INTER_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.MAX_GAP_WATER_FLOOR
import common.hoangdz.admob.config.shared.AdSharedSetting.MAX_NATIVE_AD_THRESHOLD
import common.hoangdz.admob.config.shared.AdSharedSetting.MIN_GAP_WATER_FLOOR
import common.hoangdz.admob.config.shared.AdSharedSetting.USE_WATER_FLOW
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
    override var minGapWaterFloor: Long
        get() = pref.getLong(MIN_GAP_WATER_FLOOR.first, MIN_GAP_WATER_FLOOR.second)
        set(value) {
            putLong(MIN_GAP_WATER_FLOOR.first, value)
        }

    override var maxGapWaterFloor: Long
        get() = pref.getLong(MAX_GAP_WATER_FLOOR.first, MAX_GAP_WATER_FLOOR.second)
        set(value) {
            putLong(MAX_GAP_WATER_FLOOR.first, value)
        }
    override var ignoredGapThreshold: Int
        get() = pref.getInt(IGNORED_GAP_THRESHOLD.first, IGNORED_GAP_THRESHOLD.second)
        set(value) {
            putInt(IGNORED_GAP_THRESHOLD.first, value)
        }
    override var useWaterFlow: Boolean
        get() = pref.getBoolean(USE_WATER_FLOW.first, USE_WATER_FLOW.second)
        set(value) {
            putBoolean(USE_WATER_FLOW.first, value)
        }
    override var interstitialGap: Long
        get() = if (BuildConfig.DEBUG) 0L else AdState.forceInterGap
            ?: pref.getLong(INTER_GAP.first, INTER_GAP.second)
        set(value) {
            putLong(INTER_GAP.first, value)
        }
    override var appOpenGap: Long
        get() = pref.getLong(APP_OPEN_GAP.first, APP_OPEN_GAP.second)
        set(value) {
            putLong(APP_OPEN_GAP.first, value)
        }
    override var fullScreenGap: Long
        get() = pref.getLong(FULL_SCREEN_GAP.first, FULL_SCREEN_GAP.second)
        set(value) {
            putLong(FULL_SCREEN_GAP.first, value)
        }

    private val canShowFullScreenAds
        get() = !FullScreenAdsLoader.showing

    override val canShowInterstitial: Boolean
        get() {
            return System.currentTimeMillis() - AdState.lastTimeShowInterAds > interstitialGap && System.currentTimeMillis() - AdState.lastTimeShowAppOpenAds > fullScreenGap && canShowFullScreenAds
        }
    override val canShowAppOpen: Boolean
        get() = System.currentTimeMillis() - AdState.lastTimeShowAppOpenAds > appOpenGap && System.currentTimeMillis() - AdState.lastTimeShowInterAds > fullScreenGap && canShowFullScreenAds

}