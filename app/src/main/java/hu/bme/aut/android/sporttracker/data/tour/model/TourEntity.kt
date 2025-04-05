package hu.bme.aut.android.sporttracker.data.tour.model

//import androidx.room.Entity
//import androidx.room.PrimaryKey
import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint

//@Entity(tableName = "tours")
data class TourEntity(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val totalDistance: Float, // km
    val averageSpeed: Float, // km/h
    val elevationGain: Double, // méter
    val locationHistory: List<LocationPoint>,
    val transportationMode: String?
)