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
        if (locations.isEmpty()) return 15 // Alapértelmezett zoom, ha nincs adat

        val minLat = locations.minOf { it.first }
        val maxLat = locations.maxOf { it.first }
        val minLng = locations.minOf { it.second }
        val maxLng = locations.maxOf { it.second }

        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng

        // A Google Maps zoom szintekhez igazodó értékek
        val zoomLevels = listOf(
            360.0 to 2,   // 360° → Világ nézet
            180.0 to 3,
            90.0 to 4,
            45.0 to 5,
            22.5 to 6,
            11.25 to 7,
            5.625 to 8,
            2.8125 to 9,
            1.40625 to 10,
            0.703125 to 11,
            0.3515625 to 12,
            0.17578125 to 13,
            0.087890625 to 14,
            0.0439453125 to 15,  // Városi utcaszint
            0.02197265625 to 16,
            0.010986328125 to 17,
            0.0054931640625 to 18,
            0.00274658203125 to 19,
            0.001373291015625 to 20  // Maximális nagyítás
        )

        // A legnagyobb különbséget vesszük figyelembe (hosszabbik tengely)
        val maxDiff = maxOf(latDiff, lngDiff)

        // Kiválasztjuk a megfelelő zoom szintet
        return zoomLevels.last { it.first >= maxDiff }.second
    }
    fun getStaticMapUrl2(locations: List<Pair<Double, Double>>): String {
        val apiKey = BuildConfig.MAPS_API_KEY

        if (locations.isEmpty()) return ""

        val center = "${locations.first().first},${locations.first().second}"

        // Az útvonal helyes formátumban
        val path = locations.joinToString("|") { "${it.first},${it.second}" }

        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=640x500&center=$center&zoom=18" +
                "&path=color:0x0000FF|weight:4|$path" +
                "&key=$apiKey"
    }

    fun getStaticMapUrl(locations: List<Pair<Double, Double>>): String {
        val apiKey = BuildConfig.MAPS_API_KEY

        if (locations.isEmpty()) return ""

        val center = "${locations.first().first},${locations.first().second}"
        val path = locations.joinToString("|") { "${it.first},${it.second}" }

        val zoom = calculateZoomLevel(locations) // Dinamikusan számított zoom szint

        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=640x500&center=$center&zoom=$zoom" +
                "&path=color:0x800080FF|weight:6|$path" + // Élénkebb lila vonal
                "&key=$apiKey"
    }
}