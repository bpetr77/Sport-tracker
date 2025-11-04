package hu.bme.aut.android.sporttracker.domain.repository

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.BoundingBox
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.RouteSegment

/**
 * A GraphRepository interfész a gráf adatok elérésének absztrakciója.
 * A domain réteg számára biztosít egységes hozzáférést a gráfhoz,
 * függetlenül attól, hogy az adatbázisból, hálózatról vagy más forrásból érkezik.
 */
interface GraphRepository {
    suspend fun loadSubGraph(boundingBox: BoundingBox): Graph

    suspend fun saveGraphFromSegments(segments: List<RouteSegment>, batchSize: Int = 50)

    suspend fun clearGraphData()

    suspend fun countNodes(): Int

    suspend fun getNodesInRotatedBox(bbox: BoundingBox): List<NodeEntity>

    fun pointInPolygon(lat: Double, lon: Double, polygon: List<LatLng>): Boolean
}