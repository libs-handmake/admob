package common.hoangdz.admob.ad_format.full_screen_native_ads.config

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
class FullscreenNativeConfig(
    @SerializedName("enabled") val enable: Boolean = true,
    @SerializedName("duration_in_seconds") val durationInSeconds: Int = 5
)