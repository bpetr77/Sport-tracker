package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel

@Composable
fun UserInputTextField(viewModel: TourSettingsViewModel) {
    val commentInput by viewModel.commentInput.collectAsState()
    OutlinedTextField(
        value = commentInput,
        onValueChange = { viewModel.updateComment(it) },
        label = { Text("Jegyzet a túrához") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        maxLines = 5
    )
}