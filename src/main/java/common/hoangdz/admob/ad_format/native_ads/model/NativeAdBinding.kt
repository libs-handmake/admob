package common.hoangdz.admob.ad_format.native_ads.model

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.nativead.NativeAd
import common.hoangdz.admob.ad_format.native_ads.loader.NativeAdQueue
import common.hoangdz.admob.di.entry_point.AdmobEntryPoint
import common.hoangdz.lib.extensions.appInject
import common.hoangdz.lib.viewmodels.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NativeAdBinding(
    private val context: Context,
    private val id: String,
    private val scope: CoroutineScope,
    private val reloadable: Boolean = false
) : LifecycleEventObserver {

    private var timeLoaded: Long = 0

    val nativeAdState by lazy { MutableStateFlow(DataResult<NativeAd>(DataResult.DataState.IDLE)) }

    private val admobEntryPoint by lazy { context.appInject<AdmobEntryPoint>() }

    private val nativeAdsLoader by lazy { admobEntryPoint.nativeAdsLoader() }

    private val intervalToReload = 5_000

    private var reloadJob: Job? = null

    private var currentOwner: LifecycleOwner? = null

    private var paused = false

    fun attachToLifeCycle(owner: LifecycleOwner) {
        currentOwner?.lifecycle?.removeObserver(this)
        owner.lifecycle.removeObserver(this)
        currentOwner = owner
    }

    init {
        scope.launch {
            nativeAdState.collect {
                if (it.state == DataResult.DataState.LOADED && it.value != null) {
                    timeLoaded = System.currentTimeMillis()
                    if (!paused) reloadAd()
                }
            }
        }
    }

    fun startReload() {
        stopReload()
        if (nativeAdState.value.state == DataResult.DataState.LOADING) return
        if (!reloadable && nativeAdState.value.state != DataResult.DataState.IDLE) return
        reloadJob = scope.launch {
            val time = intervalToReload - (System.currentTimeMillis() - timeLoaded)
            if (time > 0) delay(time)
            reloadAd()
        }
    }

    private fun reloadAd() {
        val nativeAd = nativeAdState.value.value
        nativeAdState.value = DataResult(DataResult.DataState.IDLE)
        nativeAd?.destroy()
        nativeAdsLoader.enqueueNativeAds(NativeAdQueue(id, nativeAdState))
    }

    fun stopReload() {
        reloadJob?.cancel()
    }

    fun destroy() {
        nativeAdsLoader.removeQueue(id)
        nativeAdState.value.value?.destroy()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
            paused = true
            stopReload()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            paused = false
            startReload()
        }
    }


}