package hu.bme.aut.android.sporttracker.data.remote.firebase.model

data class FirebaseLocationPoint(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0,
    var timestamp: Long = 0L
)