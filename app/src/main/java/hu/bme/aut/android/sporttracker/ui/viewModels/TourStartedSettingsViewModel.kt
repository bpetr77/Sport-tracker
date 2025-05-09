package hu.bme.aut.android.sporttracker.ui.viewModels

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.database.DatabaseProvider
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.data.tour.repository.TourRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TourStartedSettingsViewModel(
    private val locationRepository: LocationRepository,
    private val tourUseCase: TourUseCase
) : ViewModel() {

    private val _locationHistory = MutableStateFlow<List<LocationPoint>>(emptyList())
    val locationHistory = _locationHistory.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _speedHistory = MutableStateFlow<List<Double>>(emptyList())
    val speedHistory = _speedHistory.asStateFlow()

    private val _totalDistance = MutableStateFlow(0f)
    val totalDistance = _totalDistance.asStateFlow()

    private val _currentSpeed = MutableStateFlow(0f) // m/s
    val currentSpeed = _currentSpeed.asStateFlow()

    val _segments = MutableStateFlow<List<MutableList<LocationPoint>>>(listOf(mutableListOf()))
    val segments: StateFlow<List<List<LocationPoint>>> = _segments

    init {
        viewModelScope.launch {
            locationRepository.locations.collect { locations ->
                _locationHistory.value = locations
                _totalDistance.value = tourUseCase.calculateTotalDistance(locations)
                _currentSpeed.value = tourUseCase.getCurrentSpeedInKmH(locations)
                _speedHistory.value = _speedHistory.value + _currentSpeed.value.toDouble()

//                locations.forEach { location ->
//                    addLocation(location)
//                }
            }
        }
    }
    fun addLocation(location: LocationPoint) {
        if (_isPaused.value) return

        val currentSegments = _segments.value.toMutableList()
        currentSegments.last().add(location)
        _segments.value = currentSegments
    }

    fun pause() {
        _isPaused.value = true
    }

    fun resume() {
        //_isPaused.value = false
        _segments.value = _segments.value + listOf(mutableListOf()) // új szegmens
    }

    fun toggleTourPaused() {
        _isPaused.value = !_isPaused.value
    }

    fun stopTour() {
        _locationHistory.value = emptyList()
        _totalDistance.value = 0f
        _currentSpeed.value = 0f
        _speedHistory.value = emptyList()
        _isPaused.value = false
    }

    fun getAverageSpeed(): Float {
        return tourUseCase.getAverageSpeed(_totalDistance.value, _locationHistory.value)
    }

    fun getDuration(): Long {
        return tourUseCase.getDuration(_locationHistory.value)
    }

    fun calculateAllElevation(): Double {
        return tourUseCase.calculateAllElevation(_locationHistory.value)
    }

    fun getSpeedHistory(): List<Double> {
        return speedHistory.value
    }

    fun getTour(selectedTransportMode: String?, weather: String?, Comment: String?) = tourUseCase.createTourEntity(
        _locationHistory.value,
        selectedTransportMode,
        weather,
        Comment
    )
}
