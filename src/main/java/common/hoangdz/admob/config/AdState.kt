package common.hoangdz.admob.config

import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import common.hoangdz.admob.ad_format.listener.AdLoaderListener


object AdState {
    var lastTimeShowInterAds = 0L

    var lastTimeShowAppOpenAds = 0L

    var overrideInterId: String? = null

    var onPaidEvent: ((AdValue, ResponseInfo) -> Unit)? = null

    var globalInterListener: AdLoaderListener? = null

    var forceInterGap: Long? = null

}