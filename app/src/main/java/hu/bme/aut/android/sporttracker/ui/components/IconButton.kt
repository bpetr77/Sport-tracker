package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R

@Composable
fun IconButton(
    onClick: () -> Unit,
    icon: String,
    modifier: Modifier = Modifier
) {
    val iconResId = when (icon) {
        "location" -> R.drawable.baseline_my_location_24
        "start" -> R.drawable.baseline_start_24
        "message" -> R.drawable.baseline_message_24
        // Add more mappings as needed
        else -> R.drawable.ic_launcher_foreground  // Fallback icon
    }

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Icon: $icon",
            modifier = Modifier.size(26.dp)
        )
    }
}