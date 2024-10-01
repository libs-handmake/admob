package common.hoangdz.admob.ad_format.banner.state_holder

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ScreenBannerState(
    @SerializedName("show_banner") val showBanner: Boolean,
    @SerializedName("use_collapsed") val useCollapsed: Boolean
)