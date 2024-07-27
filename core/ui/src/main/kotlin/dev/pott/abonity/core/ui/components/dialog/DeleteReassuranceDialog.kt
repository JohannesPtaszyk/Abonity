package dev.pott.abonity.core.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

@Composable
fun DeleteReassuranceDialog(
    subscription: Subscription,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = rememberVectorPainter(image = AppIcons.Delete),
                contentDescription = null,
            )
        },
        title = {
            Text(stringResource(id = R.string.subscription_delete_dialog_title))
        },
        text = {
            Text(
                stringResource(
                    id = R.string.subscription_delete_dialog_text,
                    subscription.name,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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
}
