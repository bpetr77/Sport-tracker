package hu.bme.aut.android.sporttracker.domain.usecase

import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import kotlin.math.*

class TourUseCase {

    fun calculateTotalDistance(locations: List<LocationPoint>): Float {
        var totalDistance = 0f
        for (i in 1 until locations.size) {
            totalDistance += haversineDistance(locations[i - 1], locations[i])
        }
        return (totalDistance * 100).roundToInt() / 100f // Két tizedesre kerekítve
    }

    fun getAverageSpeed(totalDistance: Float, locations: List<LocationPoint>): Float {
        if (locations.size < 2) return 0f

        val firstLocation = locations.first()
        val lastLocation = locations.last()

        val totalTimeSeconds = (lastLocation.timestamp - firstLocation.timestamp) / 1000.0
        return if (totalTimeSeconds > 0) (totalDistance / totalTimeSeconds * 3.6).toFloat() else 0f
    }

    fun getDuration(locations: List<LocationPoint>): Long {
        if (locations.size < 2) return 0

        val firstLocation = locations.first()
        val lastLocation = locations.last()

        return (lastLocation.timestamp - firstLocation.timestamp) / 1000 // millis --> sec
    }

    fun calculateAllElevation(locations: List<LocationPoint>): Double {
        var elevation = 0.0
        for (i in 1 until locations.size) {
            val firstAltitude = locations[i - 1].altitude
            val lastAltitude = locations[i].altitude
            elevation += (firstAltitude - lastAltitude).absoluteValue
        }
        return elevation
    }

    fun getCurrentSpeedInKmH(locations: List<LocationPoint>): Float {
        if (locations.size < 2) return 0f

        val secondLastLocation = locations[locations.size - 2]
        val lastLocation = locations.last()

        val distance = haversineDistance(secondLastLocation, lastLocation)
        val timeSeconds = (lastLocation.timestamp - secondLastLocation.timestamp) / 1000.0

        return if (timeSeconds > 0) ((distance / timeSeconds * 3.6) * 100).roundToInt() / 100f else 0f
    }

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
}
