package dev.pott.abonity.core.ui.components.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AdCard(adId: AdId, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        AdBanner(
            adId = adId,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
