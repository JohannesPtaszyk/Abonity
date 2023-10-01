package dev.pott.abonity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.appNavGraph
import dev.pott.abonity.navigation.components.AppBottomBar
import dev.pott.abonity.navigation.components.AppNavigationRail
import dev.pott.abonity.navigation.components.AppPermanentDrawerSheet
import dev.pott.abonity.navigation.components.BottomBarItem
import dev.pott.abonity.navigation.components.DrawerItem
import dev.pott.abonity.navigation.components.NavigationType
import dev.pott.abonity.navigation.components.RailItem
import dev.pott.abonity.navigation.components.rememberNavigationType
import dev.pott.abonity.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppMainContent(this) }
    }
}