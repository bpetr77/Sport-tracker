package hu.bme.aut.android.sporttracker.domain.repository

import hu.bme.aut.android.sporttracker.domain.model.routePlanner.RouteSegment


interface OSMRepository {
    fun loadOsmData(): List<RouteSegment>
}