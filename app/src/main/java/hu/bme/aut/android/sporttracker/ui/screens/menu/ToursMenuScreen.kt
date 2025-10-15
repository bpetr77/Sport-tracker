package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.data.local.tour.database.DatabaseProvider
import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.repository.location.TourRepository
import hu.bme.aut.android.sporttracker.ui.components.TourElement
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout
import hu.bme.aut.android.sporttracker.ui.sign_in.UserData
import hu.bme.aut.android.sporttracker.ui.viewModels.TourStartedSettingsViewModel

@Composable
fun TourMenuScreen(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onTourClick: (Long) -> Unit,
    onAllToursClick: () -> Unit,
    userData: UserData?,
    onSignOut: () -> Unit,
    user: String,
    tourViewModel: TourStartedSettingsViewModel,
) {
    MainLayout(
        iconTint = Color.White,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick,
        userData = userData,
        onSignOut = onSignOut,
    ) {
        val backgroundColor = Color(0xFF255F38) // Set unique background color
        var tours by remember { mutableStateOf(emptyList<TourEntity>()) }
        var userTours by remember { mutableStateOf(emptyList<TourEntity>()) }

        // TODO: Move this to a repository or a provider or somewhere else
        val database = DatabaseProvider.getDatabase(LocalContext.current)
        val tourRepository = TourRepository(database.tourDao())

        LaunchedEffect(user) {
            //userTours = tourViewModel.getAllToursById(user)
            tours = tourViewModel.getAllToursById(user)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF18230F))
        ) {
            Column {
                Spacer(modifier = Modifier.height(45.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.menu_completed_tours),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 24.sp
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tours) { tour: TourEntity ->
                        Log.w("HALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", "Tour: $tour")
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable {
                                    onTourClick(tour.id)
                                },
                            shape = MaterialTheme.shapes.medium,
                            shadowElevation = 4.dp,
                            color = backgroundColor
                        ) {
                            TourElement(tour)
                        }
                    }
                }
            }
        }
    }
}