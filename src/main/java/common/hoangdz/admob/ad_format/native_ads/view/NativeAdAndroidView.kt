package common.hoangdz.admob.ad_format.native_ads.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import common.hoangdz.lib.R
import common.hoangdz.lib.extensions.gone
import common.hoangdz.lib.extensions.layoutInflater
import common.hoangdz.lib.extensions.logError
import common.hoangdz.lib.extensions.visible

class NativeAdAndroidView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var nativeAd: NativeAd? = null

    private var nativeAdBindingView: NativeAdView? = null

    private var headLineBindingView: TextView? = null

    private var contentBindingView: TextView? = null

    private var mediaBindingView: MediaView? = null

    private var iconBindingView: ImageView? = null

    private var callToActionBindingView: TextView? = null

    private var adLabelView: TextView? = null

    private var res: Int? = null

    fun setContentView(@LayoutRes layoutRes: Int) {
        res = layoutRes
    }

    private fun bindView() {
        adLabelView = findViewById(R.id.tv_ad_label)
        nativeAdBindingView = findViewById(R.id.native_ad_view)
        headLineBindingView = findViewById(R.id.head_line_view)
        contentBindingView = findViewById(R.id.content_view)
        iconBindingView = findViewById(R.id.icon_view)
        mediaBindingView = findViewById(R.id.media_view)
        callToActionBindingView = findViewById(R.id.call_to_action_view)
    }

    fun bindAds(nativeAd: NativeAd?) {
        synchronized(this) {
            if (nativeAd != null && this.nativeAd == nativeAd) return
            this.nativeAd = nativeAd
            if (res == null) {
                logError("Not found template layout id ")
            }
            prepare()
            nativeAd ?: kotlin.run {
                logError("Native ad not available")
                gone()
                return
            }
            startBindAds(nativeAd)
        }
    }

    private fun startBindAds(nativeAd: NativeAd?) {
        nativeAd ?: kotlin.run {
            gone()
            return
        }
        visible()
        contentBindingView?.text = nativeAd.body
        headLineBindingView?.text = nativeAd.headline
//        nativeAd.advertiser?.let {
//            adLabelView?.visible()
//            adLabelView?.text = it
//        } ?: adLabelView?.gone()
        nativeAd.icon?.let {
            iconBindingView?.visible()
            iconBindingView?.setImageDrawable(it.drawable)
        } ?: iconBindingView?.gone()
        nativeAd.mediaContent?.let {
            mediaBindingView?.visible()
            mediaBindingView?.mediaContent = it
        } ?: mediaBindingView?.gone()
        nativeAd.callToAction?.let {
            callToActionBindingView?.visible()
            callToActionBindingView?.text = it
        } ?: callToActionBindingView?.gone()
        nativeAdBindingView?.apply {
//            advertiserView = this@NativeAdAndroidView.adLabelView
            bodyView = contentBindingView
            headlineView = this@NativeAdAndroidView.headLineBindingView
            iconView = this@NativeAdAndroidView.iconBindingView
            mediaView = this@NativeAdAndroidView.mediaBindingView
            callToActionView = this@NativeAdAndroidView.callToActionBindingView
            setNativeAd(nativeAd)
        }
    }

    private fun prepare() {
        removeAllViews()
        val view = context.layoutInflater.inflate(res ?: return, this, false)
        view.visible()
        addView(view)
        bindView()
    }
}