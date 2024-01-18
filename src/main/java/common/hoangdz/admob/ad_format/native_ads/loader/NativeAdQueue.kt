package common.hoangdz.admob.ad_format.native_ads.loader

import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.lib.viewmodels.DataResult
import kotlinx.coroutines.flow.MutableStateFlow

data class NativeAdQueue(
    val queueID: String, val adState: MutableStateFlow<DataResult<NativeAd>>
)