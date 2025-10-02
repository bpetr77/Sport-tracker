package hu.bme.aut.android.sporttracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.bme.aut.android.sporttracker.data.model.LocationPoint

@Entity(tableName = "tours")
data class TourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val startTime: Long,
    val endTime: Long,
    val totalDistance: Float, // km
    val averageSpeed: Float, // km/h
    val elevationGain: Double, // m
    val locationHistory: List<LocationPoint>,
    val transportationMode: String?,
    val weatherCondition: String?,
    val comment: String?
)