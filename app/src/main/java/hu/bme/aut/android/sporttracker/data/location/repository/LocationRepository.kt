package hu.bme.aut.android.sporttracker.data.location.repository

import hu.bme.aut.android.sporttracker.data.location.model.LocationPoint

object LocationRepository {
    private val _points = mutableListOf<LocationPoint>()

    fun addPoint(point: LocationPoint) {
        _points.add(point)
    }

    fun getAllPoints(): List<LocationPoint> = _points.toList()

    fun clear() {
        _points.clear()
    }
}