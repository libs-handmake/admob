package common.hoangdz.admob.config.ad_id

open class ADIdsDebug : AdIds {
    override val bannerID: String
        get() = "ca-app-pub-3940256099942544/6300978111"
    override val nativeID: String
        get() = "ca-app-pub-3940256099942544/2247696110"
    override val interstitialID: String
        get() = "ca-app-pub-3940256099942544/1033173712"
    override val interHighFloorID: String
        get() = interstitialID
    override val interMediumFloorId: String
        get() = interstitialID
    override val interAllPriceFloorId: String
        get() = interstitialID
    override val interRewardID: String
        get() = ""
    override val rewardID: String
        get() = "ca-app-pub-3940256099942544/5224354917"
    override val appOpenID: String
        get() = "ca-app-pub-3940256099942544/9257395921"
    override val nativeFullScreen: String
        get() = "ca-app-pub-3940256099942544/7342230711"
}