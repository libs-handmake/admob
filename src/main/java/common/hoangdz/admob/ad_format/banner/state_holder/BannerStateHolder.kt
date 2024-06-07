package common.hoangdz.admob.ad_format.banner.state_holder

import common.hoangdz.lib.jetpack_compose.exts.compareAndSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerStateHolder @Inject constructor() {
    private val _refreshBannerNotifier by lazy { MutableStateFlow(0) }
    val refreshBannerNotifier by lazy { _refreshBannerNotifier.asStateFlow() }

    var useCollapsible = true

    fun refreshBanner(useCollapsible: Boolean = true) {
        this.useCollapsible = useCollapsible
        _refreshBannerNotifier.compareAndSet(1 - _refreshBannerNotifier.value)
    }
}