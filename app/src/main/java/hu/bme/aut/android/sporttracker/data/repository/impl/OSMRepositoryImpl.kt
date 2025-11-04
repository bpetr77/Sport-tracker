package hu.bme.aut.android.sporttracker.data.repository.impl

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.RouteSegment
import hu.bme.aut.android.sporttracker.domain.repository.OSMRepository

class OSMRepositoryImpl(private val context: Context) : OSMRepository {
    override fun loadOsmData(): List<RouteSegment> {
        val inputStream = context.assets.open("osm_Budapest.json")
        val json = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val listType = object : TypeToken<List<RouteSegment>>() {}.type
        return gson.fromJson(json, listType)
    }
}