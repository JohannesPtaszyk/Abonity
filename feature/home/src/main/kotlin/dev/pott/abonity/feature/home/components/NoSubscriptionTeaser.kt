package dev.pott.abonity.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.preview.PreviewCommonUiConfig
import dev.pott.abonity.core.ui.theme.AppTheme

@Composable
fun NoSubscriptionTeaser(onAddNewSubscriptionClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.rocket),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.no_subscription_teaser_title),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onAddNewSubscriptionClicked) {
            Text(text = stringResource(id = R.string.no_subscription_teaser_cta))
        }
    }
}

@PreviewCommonUiConfig
@PreviewCommonScreenConfig
@Composable
private fun NoUpcomingSubscriptionTeaserPreview() {
    AppTheme {
        NoSubscriptionTeaser(onAddNewSubscriptionClicked = {})
    }
}
