package common.hoangdz.admob.config

import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo


object AdState {
    var lastTimeShowInterAds = 0L

    var lastTimeShowAppOpenAds = 0L

    var onPaidEvent: ((AdValue, ResponseInfo) -> Unit)? = null

    var forceInterGap: Long? = null

}