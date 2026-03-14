package dev.pott.abonity.feature.subscription.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.NotificationConfig
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

private val NOTIFICATION_OPTIONS = listOf(null, 0, 1, 2, 3, 7)

@Composable
fun NotificationConfigDialog(
    currentConfig: NotificationConfig?,
    onConfirm: (NotificationConfig?) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = rememberVectorPainter(image = AppIcons.Notification),
                contentDescription = null,
            )
        },
        title = {
            Text(stringResource(id = R.string.subscription_notification_dialog_title))
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                NOTIFICATION_OPTIONS.forEach { days ->
                    val label = when (days) {
                        null -> stringResource(id = R.string.subscription_notification_dialog_off)
                        0 -> stringResource(id = R.string.subscription_notification_dialog_same_day)
                        1 -> stringResource(id = R.string.subscription_notification_dialog_one_day_before)
                        7 -> stringResource(id = R.string.subscription_notification_dialog_one_week_before)
                        else -> pluralStringResource(
                            id = R.plurals.subscription_notification_dialog_days_before,
                            count = days,
                            days,
                        )
                    }
                    val isSelected = currentConfig?.daysBeforePayment == days
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(days?.let { NotificationConfig(it) }) }
                            .padding(vertical = 4.dp),
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onConfirm(days?.let { NotificationConfig(it) }) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.dialog_btn_dismiss_default))
            }
        },
        onDismissRequest = onDismiss,
    )
}
