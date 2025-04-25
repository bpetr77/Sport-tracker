package hu.bme.aut.android.sporttracker.data.tour.model

//import androidx.room.Entity
//import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint
import kotlin.random.Random

@Entity(tableName = "tours")
data class TourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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