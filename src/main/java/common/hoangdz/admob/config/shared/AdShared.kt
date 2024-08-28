package common.hoangdz.admob.config.shared

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

    var nativeAdConfigJson: String

    val nativeAdConfig: Map<String, Boolean>
}