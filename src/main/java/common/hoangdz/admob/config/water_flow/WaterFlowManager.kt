package common.hoangdz.admob.config.water_flow

import android.content.Context
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject
import kotlin.math.max
import kotlin.math.min

class WaterFlowManager(
    private val context: Context, private val adIds: List<String>, private val normalIds: String
) {

    companion object {
        var forceTurnOffWaterFlow = false
    }

    private var adBlockLoaderTime = 0L

    private val adsShared by lazy { context.appInject<AdmobEntryPoint>().adsShared() }

    private var currentIndex = 0

    private var timeRequestFailed = 0L

    private var requestFailedTimes = 0

    val currentId: String
        get() {
            if (!adsShared.useWaterFlow || forceTurnOffWaterFlow) return normalIds
            return adIds[currentIndex]
        }

    val canNext get() = currentIndex < adIds.lastIndex && adsShared.useWaterFlow && !forceTurnOffWaterFlow

    fun failed() {
        requestFailedTimes++
        currentIndex = 0
        timeRequestFailed = System.currentTimeMillis()
        if (requestFailedTimes > 1) adBlockLoaderTime =
            min(max(adBlockLoaderTime, adsShared.minGapWaterFloor) * 2, adsShared.maxGapWaterFloor)
    }

    fun next() {
        if (!adsShared.useWaterFlow) {
            currentIndex = 0
            return
        }
        if (currentIndex !in adIds.indices) return
        currentIndex++
    }

    val validToRequestAds: Boolean
        get() {
            if (requestFailedTimes == 0) return true
            return System.currentTimeMillis() - timeRequestFailed > adBlockLoaderTime
        }

    fun reset() {
        requestFailedTimes = 0
        currentIndex = 0
        adBlockLoaderTime = adsShared.minGapWaterFloor
        timeRequestFailed = 0
    }

}