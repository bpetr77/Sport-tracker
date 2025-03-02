package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TransportButton(
    transportType: String,
    iconRes: Int,
    selectedTransportMode: String?,
    onTransportSelected: (String) -> Unit
) {
    val isSelected = selectedTransportMode == transportType
    val backgroundColor = if (isSelected) Color(0xFF90EE90) else Color(0xFFD3D3D3)  // Zöld ha kiválasztott, szürke ha nem

    Button(
        onClick = { onTransportSelected(transportType) },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(13.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "$transportType icon",
            modifier = Modifier.size(50.dp)
        )
    }
}