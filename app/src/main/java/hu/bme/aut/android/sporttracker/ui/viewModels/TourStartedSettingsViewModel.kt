package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.domain.model.tour.LocationPoint
import hu.bme.aut.android.sporttracker.domain.repository.LocationRepository
import hu.bme.aut.android.sporttracker.domain.repository.TourRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TourStartedSettingsViewModel(
    private val locationRepositoryImpl: LocationRepository,
    private val tourUseCase: TourUseCase,
    private val tourRepository: TourRepository,
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
            locationRepositoryImpl.locations.collect { locations ->
                _locationHistory.value = locations
                _totalDistance.value = tourUseCase.calculateTotalDistance(locations)
                _currentSpeed.value = tourUseCase.getCurrentSpeedInKmH(locations)
                _speedHistory.value = _speedHistory.value + _currentSpeed.value.toDouble()
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

    fun stopTour2() {
        _locationHistory.value = emptyList()
        _totalDistance.value = 0f
        _currentSpeed.value = 0f
        _speedHistory.value = emptyList()
        _isPaused.value = false
    }
    suspend fun getAllToursById(uid: String): List<TourEntity> {
        return tourRepository.getUserTours(uid)
    }

    suspend fun getTourByIdFromFireBase(id: Long): TourEntity? {
        return tourRepository.getTourById(id)
    }


    fun stopTour(
        selectedTransportMode: String?,
        weather: String?,
        comment: String?,
        userId: String
    ) {
        val tour = tourUseCase.createTourEntity(
            _locationHistory.value,
            selectedTransportMode,
            weather,
            comment,
            userId = userId
        )

        viewModelScope.launch {
            try {
                tourRepository.addTour(tour)

                // 2. Lokális állapot reset
                _locationHistory.value = emptyList()
                _totalDistance.value = 0f
                _currentSpeed.value = 0f
                _speedHistory.value = emptyList()
                _isPaused.value = false
                _segments.value = listOf(mutableListOf())

            } catch (e: Exception) {
                // itt lehet logolni vagy snackbar-t küldeni a UI-nak
                e.printStackTrace()
            }
        }
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

    fun getTour(
        selectedTransportMode: String?,
        weather: String?,
        Comment: String?,
        userId: String
    ) = tourUseCase.createTourEntity(
        _locationHistory.value,
        selectedTransportMode,
        weather,
        Comment,
        userId
    )
}
