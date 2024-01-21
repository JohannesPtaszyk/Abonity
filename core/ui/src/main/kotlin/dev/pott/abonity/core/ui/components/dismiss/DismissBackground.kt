package dev.pott.abonity.core.ui.components.dismiss

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDismissBackground(
    dismissState: DismissState,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val direction = dismissState.dismissDirection
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.error)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (direction == DismissDirection.StartToEnd) {
            LoadingDeleteIcon(dismissState, direction, contentDescription)
        }
        Spacer(modifier = Modifier)
        if (direction == DismissDirection.EndToStart) {
            LoadingDeleteIcon(dismissState, direction, contentDescription)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingDeleteIcon(
    dismissState: DismissState,
    direction: DismissDirection,
    contentDescription: String,
) {
    if (dismissState.isDismissed(direction)) {
        CircularProgressIndicator(
            color = LocalContentColor.current,
            modifier = Modifier.size(24.dp),
        )
    } else {
        Icon(
            Icons.Default.Delete,
            contentDescription = contentDescription,
        )
    }
}
