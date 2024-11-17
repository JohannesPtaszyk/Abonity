package dev.pott.abonity.feature.settings.main

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.ads.AdCard
import dev.pott.abonity.core.ui.components.ads.AdId
import dev.pott.abonity.core.ui.components.text.SectionHeader
import dev.pott.abonity.core.ui.string.paymentPeriodPluralRes
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.core.ui.util.rememberDefaultLocale

@Composable
fun SettingsScreen(
    openOssLicenses: () -> Unit,
    openNotificationSettings: () -> Unit,
    openConsentDialog: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onOpenOssLicensesClick = openOssLicenses,
        onThemeChange = viewModel::setTheme,
        onEnableAdaptiveColorChange = viewModel::enableAdaptiveColors,
        onPaymentPeriodChange = viewModel::setPeriod,
        onOpenNotificationSettingsClick = openNotificationSettings,
        openConsentDialog = openConsentDialog,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onOpenOssLicensesClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onEnableAdaptiveColorChange: (Boolean) -> Unit,
    onPaymentPeriodChange: (PaymentPeriod) -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    openConsentDialog: () -> Unit,
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
                SubscriptionSettings(settings, onPaymentPeriodChange)
                item { HorizontalDivider() }
                AppearanceSection(settings, onThemeChange, onEnableAdaptiveColorChange)
                item { HorizontalDivider() }
                MoreSection(
                    state,
                    onOpenNotificationSettingsClick,
                    onOpenOssLicensesClick,
                    openConsentDialog,
                )
                item { AdCard(adId = AdId.SETTINGS_BANNER) }
            }
        }
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.SubscriptionSettings(
    settings: Settings,
    onPaymentPeriodChanged: (PaymentPeriod) -> Unit,
) {
    item {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(text = stringResource(id = R.string.settings_section_subscription))
        }
    }
    item {
        val locale = rememberDefaultLocale()
        var showPaymentPeriods by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = showPaymentPeriods,
            onExpandedChange = { showPaymentPeriods = it },
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_period_item_label))
                },
                supportingContent = {
                    val labelRes = paymentPeriodPluralRes(settings.period)
                    Text(
                        text = pluralStringResource(
                            id = labelRes,
                            count = 1,
                        ).replaceFirstChar { char ->
                            if (char.isLowerCase()) {
                                char.titlecase(locale)
                            } else {
                                char.toString()
                            }
                        },
                    )
                },
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(
                            image = AppIcons.CalendarViewMonth,
                        ),
                        contentDescription = null,
                    )
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = showPaymentPeriods,
                onDismissRequest = { showPaymentPeriods = false },
            ) {
                val items = remember { PaymentPeriod.entries }
                items.forEach {
                    val labelRes = paymentPeriodPluralRes(it)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = pluralStringResource(
                                    id = labelRes,
                                    count = 1,
                                ).replaceFirstChar { char ->
                                    if (char.isLowerCase()) {
                                        char.titlecase(locale)
                                    } else {
                                        char.toString()
                                    }
                                },
                            )
                        },
                        onClick = {
                            onPaymentPeriodChanged(it)
                            showPaymentPeriods = false
                        },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.AppearanceSection(
    settings: Settings,
    onThemeChanged: (Theme) -> Unit,
    onEnableAdaptiveColorChanged: (Boolean) -> Unit,
) {
    item {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(text = stringResource(id = R.string.settings_section_appearance))
        }
    }
    item {
        var showThemeOptions by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = showThemeOptions,
            onExpandedChange = { showThemeOptions = it },
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_theme_item_label))
                },
                supportingContent = {
                    val labelRes = themeStringRes(settings.theme)
                    Text(text = stringResource(id = labelRes))
                },
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(
                            image = AppIcons.BrightnessMedium,
                        ),
                        contentDescription = null,
                    )
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = showThemeOptions,
                onDismissRequest = { showThemeOptions = false },
            ) {
                val items = remember { Theme.entries }
                items.forEach {
                    val labelRes = themeStringRes(it)
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = labelRes))
                        },
                        onClick = {
                            onThemeChanged(it)
                            showThemeOptions = false
                        },
                    )
                }
            }
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(
                            id = R.string.settings_adaptive_colors_item_label,
                        ),
                    )
                },
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(image = AppIcons.ColorLens),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Switch(
                        checked = settings.enableAdaptiveColors,
                        onCheckedChange = onEnableAdaptiveColorChanged,
                    )
                },
            )
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.MoreSection(
    state: SettingsState,
    onOpenNotificationSettingsClick: () -> Unit,
    onOpenOssLicensesClick: () -> Unit,
    openConsentDialog: () -> Unit,
) {
    item {
        SectionHeader(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = stringResource(id = R.string.settings_section_more))
        }
    }
    item {
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_notification_item_label))
            },
            leadingContent = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Notification),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable { onOpenNotificationSettingsClick() },
        )
    }
    item {
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_oss_item_label))
            },
            leadingContent = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Legal),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable { onOpenOssLicensesClick() },
        )
    }
    item {
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_consent_item_label))
            },
            leadingContent = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Cookie),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable { openConsentDialog() },
        )
    }
    item {
        val uriHandler = LocalUriHandler.current
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_privacy_policy_item_label))
            },
            leadingContent = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Legal),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable { uriHandler.openUri(state.privacyPolicyUrl) },
        )
    }
    item {
        val uriHandler = LocalUriHandler.current
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_imprint_item_label))
            },
            leadingContent = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Legal),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable { uriHandler.openUri(state.imprintUrl) },
        )
    }
}

@Composable
private fun themeStringRes(it: Theme) =
    when (it) {
        Theme.FOLLOW_SYSTEM -> R.string.settings_theme_follow_system
        Theme.LIGHT -> R.string.settings_theme_light
        Theme.DARK -> R.string.settings_theme_dark
    }
