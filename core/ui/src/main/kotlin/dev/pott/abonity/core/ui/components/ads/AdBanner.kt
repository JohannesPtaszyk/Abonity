package dev.pott.abonity.core.ui.components.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

private const val DEFAULT_MAX_HEIGHT = 100

@Composable
fun AdBanner(
    adId: AdId,
    modifier: Modifier = Modifier,
    defaultMaxHeight: Int = DEFAULT_MAX_HEIGHT,
) {
    BoxWithConstraints(modifier) {
        val bannerWidth = maxWidth.value.toInt()
        val bannerHeight = if (maxHeight.value.toInt() > defaultMaxHeight) {
            defaultMaxHeight
        } else {
            maxHeight.value.toInt()
        }
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    adUnitId = adId.adId

                    val adSize = AdSize.getInlineAdaptiveBannerAdSize(bannerWidth, bannerHeight)
                    setAdSize(adSize)

                    val adRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                }
            },
        )
    }
}
