package hu.bme.aut.android.sporttracker.ui.screens.Settings

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.health.connect.datatypes.ExerciseRoute
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.*

class TourStartedSettingsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _locationHistory = MutableStateFlow<List<LocationPoint>>(emptyList())
    val locationHistory = _locationHistory.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _speedHistory = MutableStateFlow<List<Float>>(listOf(0f))
    val speedHistory = _speedHistory.asStateFlow()

    //private var totalDistance = 0f
    private var _totalDistance = MutableStateFlow(0f)
    val totalDistance = _totalDistance.asStateFlow()
    // Gyorsulásmérő kezeléséhez
    //private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    //private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    fun toggleTourPaused() {
        _isPaused.value = !_isPaused.value
    }

    private var _currentSpeed = MutableStateFlow(0f) // m/s
    val currentSpeed = _currentSpeed.asStateFlow()

    //private var lastUpdateTime: Long = System.currentTimeMillis()

    fun getSpeedHistory(): List<Float> {
        return speedHistory.value
    }
    init {
        // Helymeghatározás figyelése
        viewModelScope.launch {
            locationRepository.locations.collect { locations ->
                _locationHistory.value = locations
                calculateTotalDistance()
                getCurrentSpeedInKmH()
            }
        }

        // Gyorsulásmérő figyelése
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // Lokáció frissítés
//    fun addLocationUpdate(locationPoint: LocationPoint) {
//        _locationHistory.value = _locationHistory.value + locationPoint
//        calculateTotalDistance()
//    }

    // Teljes távolság számítás GPS alapján
    fun calculateTotalDistance(): Float {
        val locations = _locationHistory.value
        _totalDistance.value = 0f
        for (i in 1 until locations.size) {
            _totalDistance.value += haversineDistance(locations[i - 1], locations[i])
        }
        _totalDistance.value = (_totalDistance.value * 100).roundToInt() / 100f // Két tizedesre kerekítve
        return totalDistance.value
    }

    fun stopTour() {
        _locationHistory.value = emptyList()
        _totalDistance.value = 0f
        _currentSpeed.value = 0f
        _speedHistory.value = emptyList()
    }
    // Átlagos sebesség számítás GPS alapján (km/h)
    fun calculateAvgSpeed(): Float {
        val locations = _locationHistory.value
        if (locations.size < 2) return 0f

        val firstLocation = locations.first()
        val lastLocation = locations.last()

        val totalTimeSeconds =
            (lastLocation.timestamp - firstLocation.timestamp) / 1000.0 // millis --> sec
        return if (totalTimeSeconds > 0) (_totalDistance.value / totalTimeSeconds * 3.6).toFloat() else 0f // km/h
    }

    fun getCurrentSpeedInKmH(): Float {
        val locations = _locationHistory.value
        if (locations.size < 2) return 0f

        val secondLastLocation = locations[locations.size - 2]
        val lastLocation = locations.last()

        val distance = haversineDistance(secondLastLocation, lastLocation)
        val timeSeconds = (lastLocation.timestamp - secondLastLocation.timestamp) / 1000.0 // millis to seconds

        if (timeSeconds > 0) _currentSpeed.value = ((distance / timeSeconds * 3.6)* 100).roundToInt() / 100f else _currentSpeed.value = 0f

        _speedHistory.value = _speedHistory.value + _currentSpeed.value
        Log.w("_speedHistory", "_speedHistory: ${_speedHistory.value}")
        return _currentSpeed.value
    }
    // Aktuális sebesség gyorsulásmérő alapján
//    fun getCurrentSpeedInKmH2(): Float {
//        return (_currentSpeed.value * 3.6f * 100).roundToInt() / 100f // Két tizedesre kerekítve
//    }

    // Haversine algoritmus a két pont közötti távolság számításához (méter)
    private fun haversineDistance(start: LocationPoint, end: LocationPoint): Float {
        val R = 6371e3 // Föld sugara méterben
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val deltaLat = Math.toRadians(end.latitude - start.latitude)
        val deltaLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (R * c).toFloat()
    }

//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
//            val acceleration = sqrt(
//                event.values[0].toDouble().pow(2) +
//                        event.values[1].toDouble().pow(2) +
//                        event.values[2].toDouble().pow(2)
//            ).toFloat()
//            Log.d("SENSOR_DEBUG", "Acceleration: $acceleration")
//            val currentTime = System.currentTimeMillis()
//            val elapsedTime = (currentTime - lastUpdateTime) / 1000.0f // másodperc
//
//            val MIN_ACCELERATION_THRESHOLD = 0.1f
//            if (acceleration < MIN_ACCELERATION_THRESHOLD) return
//
//            // Csak az aktuális sebesség számítása (nem halmozzuk)
//            if (elapsedTime > 2.2f) { // Minimum 50 ms
//                lastUpdateTime = currentTime
//                _currentSpeed.value = acceleration * elapsedTime
//            }
//        }
//    }

//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//
//    override fun onCleared() {
//        super.onCleared()
//        sensorManager.unregisterListener(this)
//    }
}