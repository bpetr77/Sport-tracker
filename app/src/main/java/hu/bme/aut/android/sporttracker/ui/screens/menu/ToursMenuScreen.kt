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
                        Divider(
                            color = if (isSystemInDarkTheme()) Color(0xFFFFF8DC) else Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}