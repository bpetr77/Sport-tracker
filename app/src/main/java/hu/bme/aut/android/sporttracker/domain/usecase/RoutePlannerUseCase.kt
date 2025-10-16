package hu.bme.aut.android.sporttracker.domain.usecase

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.model.BoundingBox
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class RoutePlannerUseCase {
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val R = 6371e3
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (R * c).toFloat()
    }

    fun calculateBoundingBox(from: LatLng, to: LatLng): BoundingBox {
        val lat1 = from.latitude
        val lon1 = from.longitude
        val lat2 = to.latitude
        val lon2 = to.longitude

        val centerLat = (lat1 + lat2) / 2
        val centerLon = (lon1 + lon2) / 2

        val distance = haversineDistance(lat1, lon1, lat2, lon2).toDouble()

        val width = distance / 2
        val height = distance * 1.5

        val latOffset = (height / 2) / 111320.0
        val lonOffset = (width / 2) / (111320.0 * cos(Math.toRadians(centerLat)))

        val minLat = centerLat - latOffset
        val maxLat = centerLat + latOffset
        val minLon = centerLon - lonOffset
        val maxLon = centerLon + lonOffset

        return BoundingBox(minLat, maxLat, minLon, maxLon)
    }
}