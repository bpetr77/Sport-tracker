package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R

@Composable
fun RouteLegend() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.legend_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LegendItem(color = Color(0xFF5BA45C), label = stringResource(R.string.legend_cycleway))
            LegendItem(color = Color(0xFF2337AD), label = stringResource(R.string.legend_footway))

            Spacer(modifier = Modifier.height(8.dp))

            LegendItem(color = Color(0xFFA7B429), label = stringResource(R.string.legend_residential))
            LegendItem(color = Color(0xFFD3BA00), label = stringResource(R.string.legend_tertiary))

            Spacer(modifier = Modifier.height(8.dp))

            LegendItem(color = Color(0xFFFF9800), label = stringResource(R.string.legend_secondary))

            Spacer(modifier = Modifier.height(8.dp))

            LegendItem(color = Color(0xFFF44336), label = stringResource(R.string.legend_primary))
            LegendItem(color = Color(0xFFB71C1C), label = stringResource(R.string.legend_trunk))
            LegendItem(color = Color.Black, label = stringResource(R.string.legend_motorway))

            Spacer(modifier = Modifier.height(8.dp))

            LegendItem(color = Color(0xFF81D4FA), label = stringResource(R.string.legend_steps))
            LegendItem(color = Color.Gray, label = stringResource(R.string.legend_unknown))
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}