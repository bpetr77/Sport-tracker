package hu.bme.aut.android.sporttracker.data.service

import android.content.Context
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.BuildConfig

object MapsService {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"
    private const val SIZE = "size=800x600"
    private const val PATH_COLOR = "color:0x0000ff|width:5"

    fun getStaticMapUrl2(locations: List<Pair<Double, Double>>): String {
        val apiKey = BuildConfig.MAPS_API_KEY
        val path = "path=$PATH_COLOR|" + locations.joinToString("|") { "${it.first},${it.second}" }
        return "center=Berkeley,CA&zoom=14&size=400x400&key=$apiKey"
    }

    fun getStaticMapUrl(locations: List<Pair<Double, Double>>): String {
        val apiKey = BuildConfig.MAPS_API_KEY

        // Ensure there are locations to process
        if (locations.isEmpty()) return ""

        // Use the first location as the center
        val center = "${locations.first().first},${locations.first().second}"

        // Build the path parameter
        val path = "path=$PATH_COLOR|" + locations.joinToString("|") { "${it.first},${it.second}" }

        // Construct the full URL
        return "$BASE_URL$SIZE&$path&center=$center&zoom=14&key=$apiKey"
    }
}