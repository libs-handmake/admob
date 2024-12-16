package common.hoangdz.admob.ad_format.full_screen_native_ads.view.screen

import android.app.Application
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.lib.viewmodels.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FullScreenNativeAdsViewModel @Inject constructor(
    application: Application, private val adShared: AdShared
) : AppViewModel(application) {
    val fullscreenNativeConfig by lazy { adShared.fullScreenNativeConfig }
}