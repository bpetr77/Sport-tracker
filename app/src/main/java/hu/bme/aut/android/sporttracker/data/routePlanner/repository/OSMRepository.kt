package hu.bme.aut.android.sporttracker.data.routePlanner.repository

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import hu.bme.aut.android.sporttracker.data.routePlanner.model.RouteSegment


class OSMRepository(private val context: Context) {
    suspend fun loadOsmData(): List<RouteSegment> {
        val inputStream = context.assets.open("osm_Budapest.json")
        val json = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val listType = object : TypeToken<List<RouteSegment>>() {}.type
        return gson.fromJson(json, listType)
    }
}
