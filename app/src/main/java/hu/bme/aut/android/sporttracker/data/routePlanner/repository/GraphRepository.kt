package hu.bme.aut.android.sporttracker.data.routePlanner.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.local.graph.database.GraphDatabase
import hu.bme.aut.android.sporttracker.data.local.graph.model.EdgeEntity
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity
import hu.bme.aut.android.sporttracker.data.model.BoundingBox
import hu.bme.aut.android.sporttracker.data.routePlanner.model.Graph
import hu.bme.aut.android.sporttracker.data.routePlanner.model.RouteSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sqrt

class GraphRepository(private val db: GraphDatabase) {

    suspend fun getNodesInRotatedBox(bbox: BoundingBox): List<NodeEntity> {
        val roughNodes = db.nodeDao().getNodesInBoundingBox(
            minLat = bbox.minLat,
            maxLat = bbox.maxLat,
            minLon = bbox.minLon,
            maxLon = bbox.maxLon
        )

        return roughNodes.filter { node ->
            pointInPolygon(node.lat, node.lon, bbox.corners)
        }
    }

    fun pointInPolygon(lat: Double, lon: Double, polygon: List<LatLng>): Boolean {
        var inside = false
        val n = polygon.size
        for (i in 0 until n) {
            val j = (i + 1) % n
            val xi = polygon[i].longitude
            val yi = polygon[i].latitude
            val xj = polygon[j].longitude
            val yj = polygon[j].latitude

            val intersects = ((yi > lat) != (yj > lat)) &&
                    (lon < (xj - xi) * (lat - yi) / ((yj - yi) + 1e-9) + xi)
            if (intersects) inside = !inside
        }
        return inside
    }

    suspend fun loadSubGraph2(
        boundingBox: BoundingBox
    ): Graph {
        val nodes = getNodesInRotatedBox(boundingBox)
        val edges = db.edgeDao().getEdgesForNodes(nodes.map { it.id })

        val graph = Graph()
        graph.buildFromEntities(nodes, edges)
        return graph
    }

    suspend fun loadSubGraph(boundingBox: BoundingBox): Graph {
        Log.d("GraphRepository", "1")
        val nodes = getNodesInRotatedBox(boundingBox)
        Log.d("GraphRepository", "2")

        val nodeIds = nodes.map { it.id }
        Log.d("GraphRepository", "3")

        val edges = getEdgesForNodesChunked(nodeIds)
        Log.d("GraphRepository", "4")

        val graph = Graph()
        Log.d("GraphRepository", "5")

        graph.buildFromEntities(nodes, edges)
        Log.d("GraphRepository", "6")

        return graph
    }

    suspend fun getEdgesForNodesChunked(
        nodeIds: List<Long>,
        chunkSize: Int = 999
    ): List<EdgeEntity> = coroutineScope {
        if (nodeIds.isEmpty()) return@coroutineScope emptyList()

        nodeIds.chunked(chunkSize)
            .map { chunk ->
                async(Dispatchers.IO) { db.edgeDao().getEdgesForNodes(chunk) }
            }
            .awaitAll()
            .flatten()
            .distinctBy { it.id }
    }

    suspend fun countNodes(): Int {
        return db.nodeDao().getCount()
    }

    suspend fun saveGraphFromSegments(segments: List<RouteSegment>, batchSize: Int = 50) {
        val graph = Graph()

        segments.chunked(batchSize).forEach {batch ->
            val nodes = mutableListOf<NodeEntity>()
            val edges = mutableListOf<EdgeEntity>()

            batch.forEach { seg ->
                val coords = seg.c
                val highwayType = seg.t
                if (coords.size < 2) return@forEach

                val oneway = seg.o
                val forwardOneWay = (oneway == "y" || oneway == "yes")
                val reverseOneWay = (oneway == "-" || oneway == "-1")
                val cycleLane = seg.b

                for (i in 0 until coords.size - 1) {
                    val (latA, lonA) = Pair(coords[i][0], coords[i][1])
                    val (latB, lonB) = Pair(coords[i + 1][0], coords[i + 1][1])
                    val dist = Graph.planarDistance(latA, lonA, latB, lonB)

                    val fromId = graph.getOrCreateNodeId(latA, lonA)
                    val toId = graph.getOrCreateNodeId(latB, lonB)

                    nodes.add(NodeEntity(fromId, latA, lonA))
                    nodes.add(NodeEntity(toId, latB, lonB))

                    when {
                        forwardOneWay -> edges.add(
                            EdgeEntity(
                                fromId = fromId,
                                toId = toId,
                                weight = dist,
                                oneway = true,
                                highwayType = highwayType,
                                cycleLane = cycleLane
                            )
                        )

                        reverseOneWay -> edges.add(
                            EdgeEntity(
                                fromId = toId,
                                toId = fromId,
                                weight = dist,
                                oneway = true,
                                highwayType = highwayType,
                                cycleLane = cycleLane
                            )
                        )

                        else -> {
                            edges.add(
                                EdgeEntity(
                                    fromId = fromId,
                                    toId = toId,
                                    weight = dist,
                                    oneway = false,
                                    highwayType = highwayType,
                                    cycleLane = cycleLane
                                )
                            )
                            edges.add(
                                EdgeEntity(
                                    fromId = toId,
                                    toId = fromId,
                                    weight = dist,
                                    oneway = false,
                                    highwayType = highwayType,
                                    cycleLane = cycleLane
                                )
                            )
                        }
                    }
                }
            }

            // Mentés batch-ben
            db.nodeDao().insertAll(nodes.distinctBy { it.id })
            db.edgeDao().insertAll(edges)
        }
    }

    suspend fun clearGraphData() {
        withContext(Dispatchers.IO) {
            db.edgeDao().deleteAll()
            db.nodeDao().deleteAll()
        }
    }

    fun Graph.Companion.planarDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dx = (lon2 - lon1) * 111_320 * cos(Math.toRadians(lat1))
        val dy = (lat2 - lat1) * 110_540
        return sqrt(dx * dx + dy * dy)
    }

}

