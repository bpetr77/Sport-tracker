package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import hu.bme.aut.android.sporttracker.ui.components.TourElement
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import hu.bme.aut.android.sporttracker.MainActivity
import hu.bme.aut.android.sporttracker.ui.navigation.Screen
import hu.bme.aut.android.sporttracker.data.tour.database.DatabaseProvider
import androidx.compose.runtime.*
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import kotlinx.coroutines.launch

@Composable
fun TourMenuScreen(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onTourClick: (Long) -> Unit,
    onAllToursClick: () -> Unit
) {
    MainLayout(
        iconTint = Color.White,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick
    ) {
        val backgroundColor = Color(0xFF255F38) // Set unique background color
        var tours by remember { mutableStateOf(emptyList<TourEntity>()) }

        // TODO: Move this to a repository or a provider or somewhere else
        val database = DatabaseProvider.getDatabase(LocalContext.current)
        val tourRepository = TourRepository(database.tourDao())

        LaunchedEffect(Unit) {
            tours = tourRepository.getAllTours()
        }
        println(tours)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF18230F)) // Set gray background color
      )  {
            Column {
                Spacer(modifier = Modifier.height(45.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Teljesített túrák",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 24.sp
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tours) { tour: TourEntity ->
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
                            println(tour)
                        }
                    }
                }
            }
        }
    }
}