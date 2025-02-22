package dev.pott.abonity.app.licenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.navigation.BackButton
import dev.pott.abonity.core.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OssLicensesScreen(navigateUp: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_oss_item_label))
                },
                navigationIcon = {
                    BackButton(navigateUp)
                },
            )
        },
    ) {
        LibrariesContainer(
            modifier = Modifier.fillMaxSize(),
            contentPadding = it,
            showDescription = true,
        )
    }
}

@Preview
@Composable
private fun OssLicensesScreenPreview() {
    AppTheme { OssLicensesScreen({}) }
}
