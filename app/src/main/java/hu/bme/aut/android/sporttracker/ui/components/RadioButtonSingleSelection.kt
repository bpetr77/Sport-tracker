package hu.bme.aut.android.sporttracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.ui.viewModels.TourSettingsViewModel
import androidx.compose.runtime.getValue
@Composable
fun RadioButtonSingleSelection(
    modifier: Modifier = Modifier,
    viewModel: TourSettingsViewModel
) {
    val radioOptions = listOf("Napos", "Felhős", "Esős", "Havas", "Szeles")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    val selectedWeather by viewModel.weatherSelection.collectAsState()

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .selectable(
                        selected = (text == selectedWeather),
                        onClick = { viewModel.updateWeatherSelection(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedWeather),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}