package hu.bme.aut.android.sporttracker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.ui.sign_in.UserData
import kotlinx.coroutines.launch


@Composable
fun MainLayout(
    iconTint: Color,
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onAllToursClick: () -> Unit,
    userData: UserData?,
    onSignOut: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (userData?.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.baseline_my_location_24)
                        )
                    }
                    if (userData?.username != null) {
                        Text(
                            text = userData.username,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                HorizontalDivider()

                // TODO: if on the map screen just close the drawer
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_map)) },
                    selected = false,
                    onClick = {
                        onMapClick()
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_completed_tours)) },
                    selected = false,
                    onClick = {
                        onToursClick()
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_all_tours_map)) },
                    selected = false,
                    onClick = {
                        onAllToursClick()
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_settings)) },
                    selected = false,
                    onClick = {
                        onMenuClick()
                        scope.launch { drawerState.close() }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_sign_out)) },
                    selected = false,
                    onClick = {
                        onSignOut()
                        scope.launch { drawerState.close() }
                    },
                )
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
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = iconTint)
            }
        }
    }
}