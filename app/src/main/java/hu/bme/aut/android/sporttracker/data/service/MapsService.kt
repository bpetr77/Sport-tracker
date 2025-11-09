package hu.bme.aut.android.sporttracker.data.service

import android.content.Context
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.BuildConfig

object MapsService {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"
    private const val SIZE = "size=1400x1200"
    private const val PATH_COLOR = "color:0x0000ff|width:5"

    //TODO: Move this to domain
    fun calculateZoomLevel(locations: List<Pair<Double, Double>>): Int {
        if (locations.isEmpty()) return 15 // Default zoom level

        val minLat = locations.minOf { it.first }
        val maxLat = locations.maxOf { it.first }
        val minLng = locations.minOf { it.second }
        val maxLng = locations.maxOf { it.second }

        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng

        val zoomLevels = listOf(
            360.0 to 1,
            180.0 to 2,
            90.0 to 3,
            45.0 to 4,
            22.5 to 5,
            11.25 to 6,
            5.625 to 7,
            2.8125 to 8,
            1.40625 to 9,
            0.703125 to 10,
            0.3515625 to 11,
            0.17578125 to 12,
            0.087890625 to 13,
            0.0439453125 to 14,
            0.02197265625 to 15,
            0.010986328125 to 16,
            0.0054931640625 to 17,
            0.00274658203125 to 18,
            0.001373291015625 to 19  // Max zoom level
        )

        val maxDiff = maxOf(latDiff, lngDiff)

        return zoomLevels.last { it.first >= maxDiff }.second
    }

    fun getStaticMapUrl(locations: List<Pair<Double, Double>>): String {
        val apiKey = BuildConfig.MAPS_API_KEY

        if (locations.isEmpty()) return ""

        val center = if (locations.isNotEmpty()) {
            val middleIndex = locations.size / 2
            "${locations[middleIndex].first},${locations[middleIndex].second}"
        } else {
            ""
        }
        val path = locations.joinToString("|") { "${it.first},${it.second}" }

        val zoom = calculateZoomLevel(locations)

        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=640x500&center=$center&zoom=$zoom" +
                "&path=color:0x800080FF|weight:6|$path" +
                "&key=$apiKey"
    }
}