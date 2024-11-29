package common.hoangdz.admob.config.remote

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import common.hoangdz.admob.config.shared.AdShared
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRemoteConfig @Inject constructor(
    private val adShared: AdShared
) {
    fun fetchRemoteConfig(
        remoteConfigDefault: Map<String, Any>,
        onRemoteFetched: (config: FirebaseRemoteConfig) -> Unit
    ) {
        val remote = Firebase.remoteConfig
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600L
            fetchTimeoutInSeconds = 15
        }
        remote.setConfigSettingsAsync(settings)
        remote.setDefaultsAsync(
            hashMapOf<String, Any>(
                MAX_NATIVE_AD_THRESHOLD.first to adShared.nativeLoaderThreshold,
                MIN_GAP_WATER_FLOOR.first to adShared.minGapWaterFloor,
                MAX_GAP_WATER_FLOOR.first to adShared.maxGapWaterFloor,
                IGNORED_GAP_THRESHOLD.first to adShared.ignoredGapThreshold,
                USE_WATER_FLOW.first to adShared.useWaterFlow,
                INTER_GAP.first to adShared.interstitialGap,
                APP_OPEN_GAP.first to adShared.appOpenGap,
                FULL_SCREEN_GAP.first to adShared.fullScreenGap,
                NATIVE_AD_CONFIG.first to adShared.nativeAdConfigJson,
                BANNER_SCREEN_CONFIG.first to adShared.bannerScreenConfigJson,
                NATIVE_RELOAD_INTERVAL.first to adShared.nativeReloadInterval,
                USE_INTER_ON_BACK.first to adShared.useInterOnBack,
                NATIVE_FULL_SCREEN_AFTER_INTER.first to adShared.nativeFullScreenAfterInter
            ).also {
                it.putAll(remoteConfigDefault)
            }.toMap()
        )
        remote.fetchAndActivate().addOnCompleteListener {
            saveRemoteData(remote)
            onRemoteFetched(remote)
        }
    }

    private fun saveRemoteData(remote: FirebaseRemoteConfig) {
        adShared.nativeLoaderThreshold = remote.getLong(MAX_NATIVE_AD_THRESHOLD.first).toInt()
        adShared.minGapWaterFloor = remote.getLong(MIN_GAP_WATER_FLOOR.first)
        adShared.maxGapWaterFloor = remote.getLong(MAX_GAP_WATER_FLOOR.first)
        adShared.ignoredGapThreshold = remote.getLong(IGNORED_GAP_THRESHOLD.first).toInt()
        adShared.useWaterFlow = remote.getBoolean(USE_WATER_FLOW.first)
        adShared.interstitialGap = remote.getLong(INTER_GAP.first)
        adShared.appOpenGap = remote.getLong(APP_OPEN_GAP.first)
        adShared.fullScreenGap = remote.getLong(FULL_SCREEN_GAP.first)
        adShared.nativeAdConfigJson = remote.getString(NATIVE_AD_CONFIG.first)
        adShared.bannerScreenConfigJson = remote.getString(BANNER_SCREEN_CONFIG.first)
        adShared.nativeReloadInterval = remote.getLong(NATIVE_RELOAD_INTERVAL.first)
        adShared.useInterOnBack = remote.getBoolean(USE_INTER_ON_BACK.first)
        adShared.nativeFullScreenAfterInter =
            remote.getBoolean(NATIVE_FULL_SCREEN_AFTER_INTER.first)
    }
}