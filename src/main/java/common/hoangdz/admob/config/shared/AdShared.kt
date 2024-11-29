package common.hoangdz.admob.config.shared

import common.hoangdz.admob.ad_format.banner.state_holder.ScreenBannerState

interface AdShared {
    var nativeLoaderThreshold: Int

    var minGapWaterFloor: Long

    var maxGapWaterFloor: Long

    var ignoredGapThreshold: Int

    var useWaterFlow: Boolean

    var interstitialGap: Long

    var appOpenGap: Long

    var fullScreenGap: Long

    val canShowInterstitial: Boolean

    val canShowAppOpen: Boolean

    var bannerScreenConfigJson: String

    var bannerScreenConfigs: Map<String, ScreenBannerState>

    var nativeAdConfigJson: String

    val nativeAdConfig: Map<String, Boolean>

    var nativeReloadInterval :Long

    var useInterOnBack:Boolean

    var nativeFullScreenAfterInter: Boolean
}