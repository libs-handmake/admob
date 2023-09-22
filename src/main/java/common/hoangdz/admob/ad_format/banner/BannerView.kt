package common.hoangdz.admob.ad_format.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdView
import com.valentinilk.shimmer.shimmer
import common.hoangdz.admob.ad_format.AdFormatViewModel
import common.hoangdz.lib.jetpack_compose.exts.collectWhenResume
import common.hoangdz.lib.viewmodels.DataResult
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BannerView(adFormatViewModel: AdFormatViewModel = hiltViewModel()) {
    val owner = LocalLifecycleOwner.current
    val loaderState = remember {
        MutableStateFlow(DataResult<AdView>(DataResult.DataState.IDLE))
    }
    val loaderStateCollection by loaderState.collectWhenResume()
    if (loaderStateCollection.state != DataResult.DataState.ERROR) {
        Box {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                return@AndroidView AdView(it).also { adView ->
                    adFormatViewModel.loadBanner(adView, loaderState, owner)
                }
            })
            if (loaderStateCollection.state == DataResult.DataState.LOADING) {
                Row {
                    Box(
                        modifier = Modifier.size(50.sdp).shimmer().background(Color.Gray)
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth().height(50.sdp).padding(start = 8.sdp)
                            .shimmer().background(Color.Gray)

                    )
                }
            }
        }
    }
}