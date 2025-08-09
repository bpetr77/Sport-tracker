package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import hu.bme.aut.android.sporttracker.ui.common.WeatherOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TourSettingsViewModel : ViewModel() {
    private val _selectedTransportMode = MutableStateFlow<String?>(null)
    val selectedTransportMode = _selectedTransportMode.asStateFlow()

    private val _isTourStarted = MutableStateFlow(false)
    val isTourStarted = _isTourStarted.asStateFlow()

    private val _weatherSelection = MutableStateFlow(WeatherOption.SUNNY.name)
    val weatherSelection: StateFlow<String> = _weatherSelection

    private val _commentInput = MutableStateFlow("")
    val commentInput = _commentInput.asStateFlow()

    fun selectTransportMode(mode: String) {
        _selectedTransportMode.value = mode
    }

    fun toggleTourState() {
        _isTourStarted.value = !_isTourStarted.value
    }

    fun updateComment(input: String) {
        _commentInput.value = input
    }

    fun updateWeatherSelection(option: String) {
        _weatherSelection.value = option
    }
}
