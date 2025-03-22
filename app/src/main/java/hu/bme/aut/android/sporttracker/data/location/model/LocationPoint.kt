package hu.bme.aut.android.sporttracker.data.location.model

import android.health.connect.datatypes.ExerciseRoute

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Long
)