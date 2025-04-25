package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TourSettingsViewModel : ViewModel() {
    private val _selectedTransportMode = MutableStateFlow<String?>(null)
    val selectedTransportMode = _selectedTransportMode.asStateFlow()

    private val _isTourStarted = MutableStateFlow(false)
    val isTourStarted = _isTourStarted.asStateFlow()

    private val _weatherSelection = MutableStateFlow("Napos")
    val weatherSelection = _weatherSelection.asStateFlow()

    private val _commentInput = MutableStateFlow("")
    val commentInput = _commentInput.asStateFlow()

//    var locationPermissionGranted = mutableStateOf(false)
//        private set
//
//    fun updatePermissionGranted(isGranted: Boolean) {
//        locationPermissionGranted.value = isGranted
//    }
    fun selectTransportMode(mode: String) {
        _selectedTransportMode.value = mode
    }

    fun toggleTourState() {
        _isTourStarted.value = !_isTourStarted.value
    }

    fun updateComment(input: String) {
        _commentInput.value = input
    }

    fun updateWeatherSelection(selection: String) {
        _weatherSelection.value = selection
    }
}
