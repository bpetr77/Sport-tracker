package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import hu.bme.aut.android.sporttracker.ui.components.TourElement
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout

//@Composable
//fun TourMenuScreen() {
//    val tours = TourRepository.getAllTours()
//    Box(modifier = Modifier.fillMaxSize()) {
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(tours) { tour ->
//                Surface(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(8.dp)
//                        .clickable {  },
//                    shape = MaterialTheme.shapes.medium,
//                    shadowElevation = 4.dp
//                ) {
//                    TourElement(tour)
//                }
//            }
//        }
//    }
//}

@Composable
fun TourMenuScreen(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit
) {
    MainLayout(
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick
    ) {
        val tours = TourRepository.getAllTours()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tours) { tour ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clickable { },
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 4.dp
                    ) {
                        TourElement(tour)
                    }
                }
            }
        }
    }
}