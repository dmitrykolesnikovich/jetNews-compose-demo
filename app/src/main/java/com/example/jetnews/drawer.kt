package com.example.jetnews

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AppDrawer(context: MainContext, currentRoute: String, closeDrawer: () -> Unit, modifier: Modifier = Modifier) {
    val navController: NavHostController = context.navController

    ModalDrawerSheet(modifier) {
        LogoLayout(Modifier.padding(horizontal = 28.dp, vertical = 24.dp))
        NavigationDrawerItem(
            label = {
                Text(stringResource(R.string.home_title))
            },
            icon = {
                Icon(Icons.Filled.Home, null)
            },
            selected = currentRoute == "home",
            onClick = {
                navController.navigateToHome()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = {
                Text(stringResource(R.string.interests_title))
            },
            icon = {
                Icon(Icons.Filled.ListAlt, null)
            },
            selected = currentRoute == "interests",
            onClick = {
                navController.navigateToInterests()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun LogoLayout(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            painterResource(R.drawable.ic_jetnews_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_jetnews_wordmark),
            contentDescription = stringResource(R.string.app_name),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
