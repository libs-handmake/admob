package common.hoangdz.admob.config.shared

import android.content.Context
import common.hoangdz.admob.ad_format.banner.state_holder.ScreenBannerState
import common.hoangdz.admob.config.AdState
import common.hoangdz.admob.config.shared.AdSharedSetting.APP_OPEN_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.BANNER_SCREEN_CONFIG
import common.hoangdz.admob.config.shared.AdSharedSetting.FULL_SCREEN_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.IGNORED_GAP_THRESHOLD
import common.hoangdz.admob.config.shared.AdSharedSetting.INTER_GAP
import common.hoangdz.admob.config.shared.AdSharedSetting.MAX_GAP_WATER_FLOOR
import common.hoangdz.admob.config.shared.AdSharedSetting.MAX_NATIVE_AD_THRESHOLD
import common.hoangdz.admob.config.shared.AdSharedSetting.MIN_GAP_WATER_FLOOR
import common.hoangdz.admob.config.shared.AdSharedSetting.NATIVE_AD_CONFIG
import common.hoangdz.admob.config.shared.AdSharedSetting.NATIVE_FULL_SCREEN_AFTER_INTER
import common.hoangdz.admob.config.shared.AdSharedSetting.NATIVE_RELOAD_INTERVAL
import common.hoangdz.admob.config.shared.AdSharedSetting.USE_INTER_ON_BACK
import common.hoangdz.admob.config.shared.AdSharedSetting.USE_WATER_FLOW
import common.hoangdz.lib.extensions.createFromJson
import common.hoangdz.lib.extensions.setBoolean
import common.hoangdz.lib.extensions.setLong
import common.hoangdz.lib.extensions.setString
import common.hoangdz.lib.extensions.toJson
import common.hoangdz.lib.utils.PreferenceHelper
import common.hoangdz.lib.utils.ads.GlobalAdState

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
        get() = AdState.forceInterGap ?: pref.getLong(INTER_GAP.first, INTER_GAP.second)
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
        get() = !GlobalAdState.showingFullScreenADS

    override val canShowInterstitial: Boolean
        get() {
            return System.currentTimeMillis() - AdState.lastTimeShowInterAds > interstitialGap && System.currentTimeMillis() - AdState.lastTimeShowAppOpenAds > fullScreenGap && canShowFullScreenAds
        }
    override val canShowAppOpen: Boolean
        get() = System.currentTimeMillis() - AdState.lastTimeShowAppOpenAds > appOpenGap && System.currentTimeMillis() - AdState.lastTimeShowInterAds > fullScreenGap && canShowFullScreenAds

    override var bannerScreenConfigJson: String
        get() = pref.getString(BANNER_SCREEN_CONFIG.first, BANNER_SCREEN_CONFIG.second)
            ?: BANNER_SCREEN_CONFIG.second
        set(value) {
            putString(BANNER_SCREEN_CONFIG.first, value)
        }

    override var bannerScreenConfigs: Map<String, ScreenBannerState>
        get() = bannerScreenConfigJson.createFromJson() ?: mapOf()
        set(value) {
            bannerScreenConfigJson = value.toJson()
        }

    override var nativeAdConfigJson: String
        get() = pref.getString(NATIVE_AD_CONFIG.first, NATIVE_AD_CONFIG.second)
            ?: NATIVE_AD_CONFIG.second
        set(value) {
            pref.setString(NATIVE_AD_CONFIG.first, value)
        }

    override var nativeAdConfig: Map<String, Boolean>
        get() = nativeAdConfigJson.createFromJson() ?: mapOf()
        private set(value) {
            nativeAdConfigJson = value.toJson()
        }
    override var nativeReloadInterval: Long
        get() = pref.getLong(NATIVE_RELOAD_INTERVAL.first, NATIVE_RELOAD_INTERVAL.second)
        set(value) {
            pref.setLong(NATIVE_RELOAD_INTERVAL.first, value)
        }
    override var useInterOnBack: Boolean
        get() = pref.getBoolean(USE_INTER_ON_BACK.first, USE_INTER_ON_BACK.second)
        set(value) {
            pref.setBoolean(USE_INTER_ON_BACK.first, value)
        }
    override var nativeFullScreenAfterInter: Boolean
        get() = pref.getBoolean(
            NATIVE_FULL_SCREEN_AFTER_INTER.first,
            NATIVE_FULL_SCREEN_AFTER_INTER.second
        )
        set(value) {
            pref.setBoolean(NATIVE_FULL_SCREEN_AFTER_INTER.first, value)
        }

}