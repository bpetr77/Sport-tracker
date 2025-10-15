package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
    onClick: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp, 60.dp, 16.dp, 0.dp)) {
        OutlinedTextField(
            value = fromText,
            onValueChange = onFromChange,
            label = { Text("From", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = toText,
            onValueChange = onToChange,
            label = { Text("To", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onClick },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(210, 210, 210, 255)
            )
        ) {
            Text("Útvonal tervezés", color = Color.Black)
        }
    }
}