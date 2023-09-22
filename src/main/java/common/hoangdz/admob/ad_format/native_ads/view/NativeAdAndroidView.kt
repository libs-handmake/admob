package common.hoangdz.admob.ad_format.native_ads.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import common.hoangdz.admob.R
import common.hoangdz.lib.extensions.gone
import common.hoangdz.lib.extensions.layoutInflater
import common.hoangdz.lib.extensions.logError
import common.hoangdz.lib.extensions.visible

class NativeAdAndroidView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var nativeAd: NativeAd? = null

    private var nativeAdView: NativeAdView? = null

    private var headLineView: TextView? = null

    private var contentView: TextView? = null

    private var mediaView: MediaView? = null

    private var iconView: ImageView? = null

    private var callToActionBtnView: Button? = null

    private var adLabelView: TextView? = null

    private var res: Int? = null

    init {
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    fun setContentView(@LayoutRes layoutRes: Int) {
        res = layoutRes
    }

    private fun bindView() {
        adLabelView = findViewById(R.id.tv_ad_label)
        nativeAdView = findViewById(R.id.native_ad_view)
        headLineView = findViewById(R.id.head_line_view)
        contentView = findViewById(R.id.content_view)
        iconView = findViewById(R.id.icon_view)
        mediaView = findViewById(R.id.media_view)
        callToActionBtnView = findViewById(R.id.call_to_action_view)
    }

    fun bindAds(nativeAd: NativeAd?) {
        this.nativeAd = nativeAd
        prepare()
        nativeAd ?: kotlin.run {
            gone()
            return
        }
        logError("bindADS")
        startBindAds(nativeAd)
    }

    private fun startBindAds(nativeAd: NativeAd?) {
        nativeAd ?: kotlin.run {
            gone()
            return
        }
        visible()
        contentView?.text = nativeAd.body
        headLineView?.text = nativeAd.headline
//        nativeAd.advertiser?.let {
//            adLabelView?.visible()
//            adLabelView?.text = it
//        } ?: adLabelView?.gone()
        nativeAd.icon?.let {
            iconView?.visible()
            iconView?.setImageDrawable(it.drawable)
        } ?: iconView?.gone()
        nativeAd.mediaContent?.let {
            mediaView?.visible()
            mediaView?.mediaContent = it
        } ?: mediaView?.gone()
        nativeAd.callToAction?.let {
            callToActionBtnView?.visible()
            callToActionBtnView?.text = it
        } ?: callToActionBtnView?.gone()
        nativeAdView?.apply {
//            advertiserView = this@NativeAdAndroidView.adLabelView
            bodyView = contentView
            headlineView = this@NativeAdAndroidView.headLineView
            iconView = this@NativeAdAndroidView.iconView
            mediaView = this@NativeAdAndroidView.mediaView
            callToActionView = this@NativeAdAndroidView.callToActionBtnView
            setNativeAd(nativeAd)
        }
    }

    private fun prepare() {
        removeAllViews()
        context.layoutInflater.inflate(res ?: return, this, true)
        bindView()
    }
}