package hu.bme.aut.android.sporttracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun MapScreenTestHost() {
    // Fake state-ek
    var showRoutePlanner by remember { mutableStateOf(false) }
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCancelButton by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {

        // Fake Map
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("map")
        )

        // ROUTE PLANNER SHEET
        if (showRoutePlanner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.DarkGray)
                    .testTag("route_planner")
            ) {

                Column {
                    Text(
                        text = fromText,
                        modifier = Modifier.testTag("from_field")
                    )
                    Text(
                        text = toText,
                        modifier = Modifier.testTag("to_field")
                    )
                }
            }
        }

        // FloatingActionButton – route planner toggle
        FloatingActionButton(
            onClick = {
                if (showCancelButton) {
                    showCancelButton = false
                } else {
                    showRoutePlanner = !showRoutePlanner
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 145.dp)
                .testTag("fab_route_planner")
        ) {
            Text("RP")
        }

        // FloatingActionButton – location button
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("fab_location")
        ) {
            Text("L")
        }

        // FloatingActionButton – start button
        FloatingActionButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_start")
        ) {
            Text("S")
        }
    }

    if (showBottomSheet) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray)
                .testTag("bottom_sheet")
        ) {
            Text("Bottom sheet content")
        }
    }
}
