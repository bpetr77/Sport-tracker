package hu.bme.aut.android.sporttracker.domain.usecase

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.model.BoundingBox
import kotlin.math.abs
import kotlin.math.asin
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

    private val SMALL_DISTANCE = 500.0

    private val MEDIUM_DISTANCE = 4000.0

    private val LARGE_DISTANCE = 12000.0

    /**
     * Kiszámol egy elforgatott határoló dobozt (Bounding Box) két pont között,
     * dinamikusan változó méretekkel a távolság függvényében.
     */
    fun calculateBoundingBox(from: LatLng, to: LatLng): BoundingBox {
        val lat1 = from.latitude
        val lon1 = from.longitude
        val lat2 = to.latitude
        val lon2 = to.longitude

        val centerLat = (lat1 + lat2) / 2.0
        val centerLon = (lon1 + lon2) / 2.0

        val distance = haversineDistance(lat1, lon1, lat2, lon2).toDouble()

        val height: Double
        val width: Double

        when {
            distance <= SMALL_DISTANCE -> {
                height = distance * 2
                width = distance * 2
            }
            distance <= MEDIUM_DISTANCE && distance > SMALL_DISTANCE -> {
                height = distance * 1.3
                width = distance / 1.8
            }
            else -> {
                height = distance * 1.1
                width = distance / 2
            }
        }

        // Kiszámolja az elforgatáshoz szükséges szöget és a sarokpontokat.

        // Lokális síkbeli vektor komponensei (méterben)
        val dxMeters = (lon2 - lon1) * 111320.0 * cos(Math.toRadians(centerLat))
        val dyMeters = (lat2 - lat1) * 111320.0

        // alap irányszög (a két pont közti vektor szöge)
        val theta = atan2(dyMeters, dxMeters)

        // A téglalap hosszabb oldala (height) lesz párhuzamos a (from -> to) vektorral.
        // Az alap téglalapunk Y-tengellyel párhuzamos, ezért PI/2-t (90 fokot) kell
        // kivonni a cél szögéből.
        val rotation = theta - Math.PI / 2.0

        // Téglalap félméretek (méterben)
        val halfW = width / 2.0
        val halfH = height / 2.0

        // Sarokpontok középponthoz viszonyítva
        val cornersLocal = listOf(
            Pair(-halfW, -halfH), // bal-alsó (x,y) méterben
            Pair(halfW, -halfH),  // jobb-alsó
            Pair(halfW, halfH),   // jobb-felső
            Pair(-halfW, halfH)   // bal-felső
        )

        val cosR = cos(rotation)
        val sinR = sin(rotation)

        // Forgatás lokális síkon (méterben), majd vissza lat/lon-ba
        val rotatedLatLon = cornersLocal.map { (xMeters, yMeters) ->
            val xRot = xMeters * cosR - yMeters * sinR
            val yRot = xMeters * sinR + yMeters * cosR

            val latRot = centerLat + (yRot / 111320.0)
            val lonRot = centerLon + (xRot / (111320.0 * cos(Math.toRadians(centerLat))))

            Pair(latRot, lonRot)
        }

        val minLat = rotatedLatLon.minOf { it.first }
        val maxLat = rotatedLatLon.maxOf { it.first }
        val minLon = rotatedLatLon.minOf { it.second }
        val maxLon = rotatedLatLon.maxOf { it.second }

        return BoundingBox(
            corners = rotatedLatLon.map { LatLng(it.first, it.second) },
            minLat = minLat,
            maxLat = maxLat,
            minLon = minLon,
            maxLon = maxLon
        )
    }
}