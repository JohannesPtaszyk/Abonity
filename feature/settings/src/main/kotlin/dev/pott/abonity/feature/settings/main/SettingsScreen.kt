package dev.pott.abonity.feature.settings.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.CalendarViewMonth
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.util.plus

@Composable
fun SettingsScreen(
    openOssLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(state = state, openOssLicenses = openOssLicenses, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    openOssLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_screen_title)) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        val settings = state.settings
        if (settings != null) {
            LazyColumn(
                contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(id = R.string.settings_theme_item_label))
                        },
                        supportingContent = {
                            Text(text = settings.theme.toString())
                        },
                        leadingContent = {
                            Icon(
                                painter = rememberVectorPainter(image = AppIcons.BrightnessMedium),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.clickable { openOssLicenses() },
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(id = R.string.settings_period_item_label))
                        },
                        supportingContent = {
                            Text(text = settings.period.toString())
                        },
                        leadingContent = {
                            Icon(
                                painter = rememberVectorPainter(image = AppIcons.CalendarViewMonth),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.clickable { openOssLicenses() },
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(id = R.string.settings_oss_item_label))
                        },
                        leadingContent = {
                            Icon(
                                painter = rememberVectorPainter(image = AppIcons.Description),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.clickable { openOssLicenses() },
                    )
                }
            }
        }
    }
}
