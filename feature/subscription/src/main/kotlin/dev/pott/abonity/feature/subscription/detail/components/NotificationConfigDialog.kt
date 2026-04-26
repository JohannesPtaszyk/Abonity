package dev.pott.abonity.feature.subscription.detail.components

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.pott.abonity.common.text.rememberDigitsFilter
import dev.pott.abonity.core.entity.subscription.NotificationConfig
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

private enum class NotificationPeriod {
    DAYS,
    WEEKS,
    MONTHS,
}

private enum class NotificationMode { OFF, SAME_DAY, BEFORE }

private const val DAYS_IN_WEEK = 7
private const val APPROX_DAYS_IN_MONTH = 30
private const val INPUT_FIELD_WEIGHT = 0.5f

private fun NotificationPeriod.labelRes(): Int =
    when (this) {
        NotificationPeriod.DAYS -> R.string.subscription_notification_dialog_period_days
        NotificationPeriod.WEEKS -> R.string.subscription_notification_dialog_period_weeks
        NotificationPeriod.MONTHS -> R.string.subscription_notification_dialog_period_months
    }

private fun initialMode(config: NotificationConfig?): NotificationMode =
    when {
        config == null -> NotificationMode.OFF
        config.daysBeforePayment == 0 -> NotificationMode.SAME_DAY
        else -> NotificationMode.BEFORE
    }

private fun initialPeriodAndCount(daysBeforePayment: Int): Pair<NotificationPeriod, Int> =
    when {
        daysBeforePayment % APPROX_DAYS_IN_MONTH == 0 &&
            daysBeforePayment >= APPROX_DAYS_IN_MONTH ->
            Pair(NotificationPeriod.MONTHS, daysBeforePayment / APPROX_DAYS_IN_MONTH)

        daysBeforePayment % DAYS_IN_WEEK == 0 && daysBeforePayment >= DAYS_IN_WEEK ->
            Pair(NotificationPeriod.WEEKS, daysBeforePayment / DAYS_IN_WEEK)

        else -> Pair(NotificationPeriod.DAYS, daysBeforePayment)
    }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun rememberRequestPermissionAndConfirm(
    onConfirm: (NotificationConfig?) -> Unit,
    onPermissionDeclined: () -> Unit,
): (NotificationConfig?) -> Unit {
    var pendingConfig by remember { mutableStateOf<NotificationConfig?>(null) }
    var isWaitingForPermission by remember { mutableStateOf(false) }

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            onPermissionResult = { isGranted ->
                if (isWaitingForPermission) {
                    isWaitingForPermission = false
                    onConfirm(pendingConfig)
                    if (!isGranted) {
                        onPermissionDeclined()
                    }
                }
            },
        )
    } else {
        null
    }

    return { config ->
        if (config != null &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            notificationPermissionState?.status?.isGranted == false
        ) {
            pendingConfig = config
            isWaitingForPermission = true
            notificationPermissionState.launchPermissionRequest()
        } else {
            onConfirm(config)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationConfigDialog(
    currentConfig: NotificationConfig?,
    onConfirm: (NotificationConfig?) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var showPermissionDeclinedDialog by remember { mutableStateOf(false) }
    val requestPermissionAndConfirm = rememberRequestPermissionAndConfirm(
        onConfirm = onConfirm,
        onPermissionDeclined = { showPermissionDeclinedDialog = true },
    )

    val (initialPeriod, initialCount) = remember(currentConfig) {
        val days = currentConfig?.daysBeforePayment?.takeIf { it > 0 } ?: 1
        initialPeriodAndCount(days)
    }
    var mode by remember { mutableStateOf(initialMode(currentConfig)) }
    var countText by remember { mutableStateOf(initialCount.toString()) }
    var period by remember { mutableStateOf(initialPeriod) }
    var periodExpanded by remember { mutableStateOf(false) }

    val periodLabel = stringResource(period.labelRes())

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
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
            NotificationModeSelector(
                mode = mode,
                onModeChange = { mode = it },
                countText = countText,
                onCountChange = { countText = it },
                onPeriodChange = { period = it },
                periodExpanded = periodExpanded,
                onPeriodExpandedChange = { periodExpanded = it },
                periodLabel = periodLabel,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when (mode) {
                        NotificationMode.OFF -> requestPermissionAndConfirm(null)

                        NotificationMode.SAME_DAY ->
                            requestPermissionAndConfirm(NotificationConfig(daysBeforePayment = 0))

                        NotificationMode.BEFORE -> {
                            val count = countText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                            val days = when (period) {
                                NotificationPeriod.DAYS -> count
                                NotificationPeriod.WEEKS -> count * DAYS_IN_WEEK
                                NotificationPeriod.MONTHS -> count * APPROX_DAYS_IN_MONTH
                            }
                            requestPermissionAndConfirm(
                                NotificationConfig(daysBeforePayment = days),
                            )
                        }
                    }
                },
            ) {
                Text(stringResource(id = R.string.dialog_btn_confirm_default))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.dialog_btn_dismiss_default))
            }
        },
        onDismissRequest = onDismiss,
    )

    if (showPermissionDeclinedDialog) {
        NotificationPermissionDeclinedDialog(
            onDismiss = { showPermissionDeclinedDialog = false },
            onOpenSettings = {
                showPermissionDeclinedDialog = false
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationModeSelector(
    mode: NotificationMode,
    onModeChange: (NotificationMode) -> Unit,
    countText: String,
    onCountChange: (String) -> Unit,
    onPeriodChange: (NotificationPeriod) -> Unit,
    periodExpanded: Boolean,
    onPeriodExpandedChange: (Boolean) -> Unit,
    periodLabel: String,
) {
    Column {
        Spacer(modifier = Modifier.height(4.dp))
        ModeRadioRow(
            selected = mode == NotificationMode.OFF,
            onSelect = { onModeChange(NotificationMode.OFF) },
            label = stringResource(id = R.string.subscription_notification_dialog_off),
        )
        Spacer(modifier = Modifier.height(8.dp))
        ModeRadioRow(
            selected = mode == NotificationMode.SAME_DAY,
            onSelect = { onModeChange(NotificationMode.SAME_DAY) },
            label = stringResource(id = R.string.subscription_notification_dialog_same_day),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BeforePaymentRow(
            selected = mode == NotificationMode.BEFORE,
            onSelect = { onModeChange(NotificationMode.BEFORE) },
            countText = countText,
            onCountChange = {
                onCountChange(it)
                onModeChange(NotificationMode.BEFORE)
            },
            onPeriodChange = {
                onPeriodChange(it)
                onModeChange(NotificationMode.BEFORE)
            },
            periodExpanded = periodExpanded,
            onPeriodExpandedChange = onPeriodExpandedChange,
            periodLabel = periodLabel,
        )
    }
}

@Composable
private fun ModeRadioRow(selected: Boolean, onSelect: () -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeforePaymentRow(
    selected: Boolean,
    onSelect: () -> Unit,
    countText: String,
    onCountChange: (String) -> Unit,
    onPeriodChange: (NotificationPeriod) -> Unit,
    periodExpanded: Boolean,
    onPeriodExpandedChange: (Boolean) -> Unit,
    periodLabel: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val digitsOnlyTextFieldFilter = rememberDigitsFilter(onCountChange)
                OutlinedTextField(
                    value = countText,
                    onValueChange = digitsOnlyTextFieldFilter,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(INPUT_FIELD_WEIGHT),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.width(8.dp))
                PeriodDropdown(
                    onPeriodChange = onPeriodChange,
                    expanded = periodExpanded,
                    onExpandedChange = onPeriodExpandedChange,
                    periodLabel = periodLabel,
                    modifier = Modifier.weight(INPUT_FIELD_WEIGHT),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    id = R.string.subscription_notification_dialog_before_payment,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodDropdown(
    onPeriodChange: (NotificationPeriod) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    periodLabel: String,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = periodLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            NotificationPeriod.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(entry.labelRes())) },
                    onClick = {
                        onPeriodChange(entry)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionDeclinedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = rememberVectorPainter(image = AppIcons.Notification),
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(id = R.string.notification_permission_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.notification_permission_dialog_text))
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(id = R.string.notification_permission_dialog_settings_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.dialog_btn_dismiss_default))
            }
        },
        onDismissRequest = onDismiss,
    )
}
