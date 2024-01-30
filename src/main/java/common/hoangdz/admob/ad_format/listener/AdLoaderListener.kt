package common.hoangdz.admob.ad_format.listener

abstract class AdLoaderListener {
    open fun onAdFailedToLoad() {}

    open fun onLoaded(){}

    open fun onAdConsume() {}

    open fun onAdClicked() {

    }

    open fun onAdStartShow() {}

    open fun onAdClosed() {}

    open fun onAdFailedToShow() {}

    open fun onInterPassed() {}

    open fun onAdStartLoad(){}
}