package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun RoutePlannerSheet(
    fromText: String,
    toText: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onClick: () -> Unit,
    onUseCurrentLocation: (() -> Unit),
    onSwap: (() -> Unit)
) {
    Column(modifier = Modifier.padding(0.dp, 60.dp, 16.dp, 0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSwap,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Felcserélés",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                OutlinedTextField(
                    value = fromText,
                    onValueChange = onFromChange,
                    label = { Text("From", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = onUseCurrentLocation ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Saját pozíció",
                                tint = Color.White
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = toText,
                    onValueChange = onToChange,
                    label = { Text("To", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    readOnly = true
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClick,
            enabled = fromText.isNotEmpty() && toText.isNotEmpty(),
            modifier = Modifier.align(Alignment.End),
            colors = if (fromText.isNotEmpty() && toText.isNotEmpty()) {
                ButtonDefaults.buttonColors(
                    containerColor = Color(210, 210, 210, 255)
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = Color(210, 210, 210, 155)
                )
            }
        ) {
            Text("Útvonal tervezés", color = Color.Black)
        }
    }
}