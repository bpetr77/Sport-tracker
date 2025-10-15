package hu.bme.aut.android.sporttracker.data.mappers

import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.remote.firebase.model.FirebaseTour

fun TourEntity.toFirebase(): FirebaseTour {
    return FirebaseTour(
        id = id.toString(),
        startTime = startTime,
        endTime = endTime,
        totalDistance = totalDistance,
        averageSpeed = averageSpeed,
        elevationGain = elevationGain,
        locationHistory = locationHistory.map { it.toFirebase() },
        transportationMode = transportationMode,
        weatherCondition = weatherCondition,
        comment = comment
    )
}

fun FirebaseTour.toEntity(): TourEntity {
    return TourEntity(
        id = id.toLong(),
        startTime = startTime,
        endTime = endTime,
        totalDistance = totalDistance,
        averageSpeed = averageSpeed,
        elevationGain = elevationGain,
        locationHistory = locationHistory.map { it.toEntity() },
        transportationMode = transportationMode,
        weatherCondition = weatherCondition,
        comment = comment,
        userId = id
    )
}