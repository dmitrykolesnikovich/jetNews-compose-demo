package com.example.jetnews

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeModel
import com.example.jetnews.ui.interests.InterestsModel
import com.example.jetnews.ui.interests.InterestsRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun NavController.navigateToHome() {
    navigate("home") {
        // Pop up to the start destination of the graph to avoid building up a large stack of destinations on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true // avoid multiple copies of the same destination when reselecting the same item
        restoreState = true // restore state when reselecting a previously selected item
    }
}

fun NavController.navigateToInterests() {
    navigate("interests") {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(context: MainContext, widthSizeClass: WindowWidthSizeClass) = AppTheme {
    val navController: NavHostController = context.navController
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val entry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentRoute: String = entry?.destination?.route ?: "home"
    val isScreenExpanded: Boolean = widthSizeClass == WindowWidthSizeClass.Expanded
    val sizeAwareDrawerState: DrawerState = rememberSizeAwareDrawerState(isScreenExpanded)

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(context, currentRoute, closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } })
        },
        drawerState = sizeAwareDrawerState,
        gesturesEnabled = !isScreenExpanded,
        content = {
            Row {
                if (isScreenExpanded) {
                    AppNavigationRail(context, currentRoute)
                }
                AppNavigationHost(
                    context,
                    startDestination = "home",
                    isScreenExpanded,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } }
                )
            }
        }
    )
}

@Composable
private fun AppNavigationHost(context: MainContext, startDestination: String, isScreenExpanded: Boolean, modifier: Modifier = Modifier, openDrawer: () -> Unit = {}) {
    NavHost(context.navController, startDestination, modifier) {
        composable("home", deepLinks = listOf(navDeepLink { uriPattern = "$applicationUri/home?postId={postId}" })) { entry ->
            val selectedPostId: String? = entry.arguments?.getString("postId")
            val model: HomeModel = viewModel(factory = HomeModel.provideFactory(context.postsRepository, selectedPostId))
            HomeRoute(model, isScreenExpanded, openDrawer)
        }
        composable("interests") {
            val model: InterestsModel = viewModel(factory = InterestsModel.provideFactory(context.interestsRepository))
            InterestsRoute(model, isScreenExpanded, openDrawer)
        }
    }
}

@Composable
fun AppNavigationRail(context: MainContext, currentRoute: String, modifier: Modifier = Modifier) {
    val navController: NavHostController = context.navController

    NavigationRail(
        header = {
            Icon(
                painter = painterResource(R.drawable.ic_jetnews_logo),
                contentDescription = null,
                modifier = Modifier.padding(vertical = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        content = {
            Spacer(Modifier.weight(1f))
            NavigationRailItem(
                selected = currentRoute == "home",
                onClick = navController::navigateToHome,
                icon = {
                    Icon(Icons.Filled.Home, stringResource(R.string.home_title))
                },
                label = {
                    Text(stringResource(R.string.home_title))
                },
                alwaysShowLabel = false
            )
            NavigationRailItem(
                selected = currentRoute == "interests",
                onClick = navController::navigateToInterests,
                icon = {
                    Icon(Icons.Filled.ListAlt, stringResource(R.string.interests_title))
                },
                label = {
                    Text(stringResource(R.string.interests_title))
                },
                alwaysShowLabel = false
            )
            Spacer(Modifier.weight(1f))
        },
        modifier = modifier,
    )
}

