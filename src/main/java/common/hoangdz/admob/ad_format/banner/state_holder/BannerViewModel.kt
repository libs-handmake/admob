package common.hoangdz.admob.ad_format.banner.state_holder

import android.app.Application
import common.hoangdz.lib.viewmodels.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(application: Application, private val bannerStateHolder: BannerStateHolder) : AppViewModel(application) {
    val refreshBannerNotifier get() = bannerStateHolder.refreshBannerNotifier

    val useCollapsible get() = bannerStateHolder.useCollapsible
}