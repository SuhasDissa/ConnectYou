package com.bnyro.contacts.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bnyro.contacts.presentation.screens.contacts.model.ContactsModel
import com.bnyro.contacts.presentation.screens.settings.model.ThemeModel
import com.bnyro.contacts.presentation.screens.sms.model.SmsModel

@Composable
fun HomeNavContainer(
    initialTab: HomeRoutes,
    onNavigate: (String) -> Unit,
    smsModel: SmsModel,
    contactsModel: ContactsModel,
    themeModel: ThemeModel
) {
    val navController = rememberNavController()

    var selectedRoute by remember {
        mutableStateOf(initialTab)
    }

    // listen for destination changes (e.g. back presses)
    DisposableEffect(Unit) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            HomeRoutes.all.firstOrNull { it.route == destination.route }
                ?.also { selectedRoute = it }
        }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    val orientation = LocalConfiguration.current.orientation
    Scaffold(
        bottomBar = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                NavigationBar(
                    tonalElevation = 5.dp
                ) {
                    HomeRoutes.all.forEach {
                        NavigationBarItem(
                            label = {
                                Text(stringResource(it.stringRes))
                            },
                            icon = {
                                Icon(it.icon, null)
                            },
                            selected = it == selectedRoute,
                            onClick = {
                                navController.popBackStack()
                                navController.navigate(it.route)
                            }
                        )
                    }
                }
            }
        }
    ) { pV ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(pV)
        ) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRail {
                    HomeRoutes.all.forEach {
                        NavigationRailItem(selected = it == selectedRoute,
                            onClick = {
                                navController.popBackStack()
                                navController.navigate(it.route)
                            },
                            icon = { Icon(it.icon, null) },
                            label = {
                                Text(stringResource(it.stringRes))
                            })
                    }
                }
            }
            HomeNavHost(
                navController = navController,
                startTab = initialTab,
                onNavigate = onNavigate,
                smsModel = smsModel,
                contactsModel = contactsModel,
                themeModel = themeModel
            )
        }
    }
}