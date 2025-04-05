package hu.bme.aut.android.sporttracker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import hu.bme.aut.android.sporttracker.ui.navigation.Screen
import hu.bme.aut.android.sporttracker.ui.screens.map.MapScreen
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel
import hu.bme.aut.android.sporttracker.ui.viewModels.LocationViewmodel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Text("Sport Tracker", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Completed Tours") },
                    selected = false,
                    onClick = {
                        onToursClick()
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Settings") },
                    selected = false,
                    onClick = {
                        onMenuClick()
                        scope.launch { drawerState.close() }
                    }
                )
                // TODO: if on the map screen just close the drawer
                NavigationDrawerItem(
                    label = { Text(text = "Map") },
                    selected = false,
                    onClick = {
                        onMapClick()
                        scope.launch { drawerState.close() }
                    }
                )
                // ...other drawer items
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                },
                modifier = Modifier.padding(16.dp, 30.dp)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
            }
        }
    }
}