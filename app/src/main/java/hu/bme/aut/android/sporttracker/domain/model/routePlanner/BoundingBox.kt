package hu.bme.aut.android.sporttracker.domain.model.routePlanner

import com.google.android.gms.maps.model.LatLng

// TODO: NO NEED FOR MIN/MAX LAT/LON VALUES
data class BoundingBox(
    val corners: List<LatLng>,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)