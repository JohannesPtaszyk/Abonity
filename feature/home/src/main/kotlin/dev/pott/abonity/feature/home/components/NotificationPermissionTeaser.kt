package dev.pott.abonity.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.preview.PreviewCommonUiConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionTeaser(
    notificationPermissionState: PermissionState,
    onCloseClick: (shouldNotShowAgain: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldNotShowAgain by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Notification),
                    contentDescription = null,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                )
                Text(
                    text = stringResource(id = R.string.notification_permission_teaser_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp).weight(1f, fill = true),
                )
                IconButton(
                    onClick = { onCloseClick(shouldNotShowAgain) },
                ) {
                    Icon(
                        rememberVectorPainter(image = AppIcons.Close),
                        null,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.notification_permission_teaser_text),
                textAlign = TextAlign.Center,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = shouldNotShowAgain,
                    onCheckedChange = { shouldNotShowAgain = it },
                )
                Text(
                    text = stringResource(id = R.string.notification_permission_teaser_checkbox),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    notificationPermissionState.launchPermissionRequest()
                },
            ) {
                Text(text = stringResource(id = R.string.notification_permission_teaser_btn))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewCommonScreenConfig
@PreviewCommonUiConfig
@Composable
private fun NotificationPermissionTeaserPreview() {
    AppTheme {
        NotificationPermissionTeaser(
            notificationPermissionState = object : PermissionState {
                override val permission: String = ""
                override val status: PermissionStatus = PermissionStatus.Denied(false)
                override fun launchPermissionRequest() {
                    // Request
                }
            },
            onCloseClick = {
                // Close clicked
            },
        )
    }
}
