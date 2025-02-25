package hu.bme.aut.android.sporttracker.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hu.bme.aut.android.sporttracker.ui.components.MenuItem

@Composable
fun MenuScreen(navController: NavController, menuItems: List<MenuItem>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        menuItems.forEach { item ->
            Button(
                onClick = { navController.navigate(item.route) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = item.title)
            }
        }
    }
}