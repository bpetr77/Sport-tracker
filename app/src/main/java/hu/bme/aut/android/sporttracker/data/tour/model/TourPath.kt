package hu.bme.aut.android.sporttracker.data.tour.model

import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint

data class TourPath(
    val segments: List<List<LocationPoint>>
)