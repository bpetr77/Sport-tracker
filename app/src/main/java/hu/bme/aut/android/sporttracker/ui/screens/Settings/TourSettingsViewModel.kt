package hu.bme.aut.android.sporttracker.ui.screens.Settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TourSettingsViewModel : ViewModel() {
    private val _selectedTransportMode = MutableStateFlow<String?>(null)
    val selectedTransportMode = _selectedTransportMode.asStateFlow()

    private val _isTourStarted = MutableStateFlow(false)
    val isTourStarted = _isTourStarted.asStateFlow()

    //TODO: Maybe not an importanat function, in the future it can be deleted
    fun selectTransportMode(mode: String) {
        _selectedTransportMode.value = mode
    }

    fun toggleTourState() {
        _isTourStarted.value = !_isTourStarted.value
    }
}
