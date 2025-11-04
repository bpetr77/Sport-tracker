package hu.bme.aut.android.sporttracker.data.mappers

import hu.bme.aut.android.sporttracker.domain.model.tour.LocationPoint
import hu.bme.aut.android.sporttracker.data.remote.firebase.model.FirebaseLocationPoint


fun LocationPoint.toFirebase(): FirebaseLocationPoint {
    return FirebaseLocationPoint(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        timestamp = timestamp
    )
}

fun FirebaseLocationPoint.toEntity(): LocationPoint {
    return LocationPoint(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        timestamp = timestamp
    )
}