package hu.bme.aut.android.sporttracker.data.remote.firebase.model

data class FirebaseTour(
    var id: String = "",
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var totalDistance: Float = 0f,
    var averageSpeed: Float = 0f,
    var elevationGain: Double = 0.0,
    var locationHistory: List<FirebaseLocationPoint> = emptyList(),
    var transportationMode: String? = null,
    var weatherCondition: String? = null,
    var comment: String? = null
)