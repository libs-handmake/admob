package common.hoangdz.admob.ad_format.full_screen_native_ads.view.screen

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import common.hoangdz.admob.R
import common.hoangdz.lib.jetpack_compose.exts.SafeModifier
import common.hoangdz.lib.jetpack_compose.exts.clickableWithDebounce
import common.hoangdz.lib.jetpack_compose.navigation.LocalScreenConfigs
import common.hoangdz.lib.jetpack_compose.navigation.ScreenConfigs
import common.hoangdz.lib.jetpack_compose.navigation.ScreenNavConfig
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


object FullScreenNativeAdRoute : ScreenNavConfig<Nothing>() {

    var onComplete: (() -> Unit)? = null

    var adContent: (@Composable () -> Unit)? = null

    override val routePattern: String
        get() = "FullScreenNativeAdScreen"


    override fun onBackPressed(activity: Activity?, configs: ScreenConfigs): Boolean {
        return false
    }

    @Composable
    override fun BuildContent(screenNavConfig: ScreenConfigs) {
        ScreenContent(adContent ?: return)
    }

}


@Composable
private fun ScreenContent(
    content: @Composable () -> Unit
) {
    val config = LocalScreenConfigs.current
    var time by remember {
        mutableIntStateOf(3)
    }
    val timer = remember {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
            }

        }
    }
    LaunchedEffect(key1 = Unit) {
        timer.start()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            timer.cancel()
        }
    }
    Scaffold {
        Box(modifier = SafeModifier.padding(it)) {
            content()
            Box(modifier = SafeModifier
                .align(Alignment.TopEnd)
                .padding(8.sdp)
                .clip(CircleShape)
                .clickableWithDebounce {
                    if (time == 0) {
                        config.pop()
                        FullScreenNativeAdRoute.onComplete?.invoke()
                        FullScreenNativeAdRoute.onComplete = null
                    }
                }
                .size(20.sdp)
                .aspectRatio(1f)
                .background(Color.Black.copy(alpha = .5f))
                .padding(4.sdp)) {
                if (time > 0) {
                    Text(
                        modifier = SafeModifier.align(Alignment.Center),
                        text = time.toString(),
                        style = TextStyle(
                            color = Color.White, fontSize = 12.ssp, textAlign = TextAlign.Center
                        ),
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "close",
                        colorFilter = ColorFilter.tint(
                            Color.White
                        )
                    )
                }
            }
        }
    }
}