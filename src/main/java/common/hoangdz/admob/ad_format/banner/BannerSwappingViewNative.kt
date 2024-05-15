package common.hoangdz.admob.ad_format.banner

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import common.hoangdz.admob.ad_format.AdFormatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class BannerSwappingViewNative : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var scope: CoroutineScope? = null

    private var banner: AdView? = null
        set(value) {
            if (value == field) return
            field?.destroy()
            removeView(field)
            field = value
        }

    fun hasBanner() = banner != null


    fun generateBanner(adFormatViewModel: AdFormatViewModel, adID: String, owner: LifecycleOwner) {
        addView(AdView(context).apply {
            adFormatViewModel.loadBanner(adID, this, true, owner, object : AdListener() {
                override fun onAdLoaded() {
                    banner = this@apply
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {

                }
            })
        })
    }

    fun resume() = banner?.resume()

    fun pause() = banner?.pause()

    fun destroy() {
        banner = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scope = CoroutineScope(Dispatchers.IO)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        banner = null
        scope?.cancel()
    }
}