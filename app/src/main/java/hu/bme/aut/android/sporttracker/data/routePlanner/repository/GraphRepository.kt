package hu.bme.aut.android.sporttracker.data.routePlanner.repository

import android.util.Log
import hu.bme.aut.android.sporttracker.data.local.graph.database.GraphDatabase
import hu.bme.aut.android.sporttracker.data.local.graph.model.EdgeEntity
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity
import hu.bme.aut.android.sporttracker.data.routePlanner.model.Graph
import hu.bme.aut.android.sporttracker.data.routePlanner.model.RouteSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sqrt

class GraphRepository(private val db: GraphDatabase) {
    suspend fun loadSubGraph(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): Graph {
        val nodes = db.nodeDao().getNodesInBoundingBox(minLat, maxLat, minLon, maxLon)
        val edges = db.edgeDao().getEdgesForNodes(nodes.map { it.id })

        val graph = Graph()
        graph.buildFromEntities(nodes, edges)
        return graph
    }

    // --- Ellenőrizni, hogy van-e már graph a DB-ben
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
                                highwayType = highwayType
                            )
                        )

                        reverseOneWay -> edges.add(
                            EdgeEntity(
                                fromId = toId,
                                toId = fromId,
                                weight = dist,
                                oneway = true,
                                highwayType = highwayType
                            )
                        )

                        else -> {
                            edges.add(
                                EdgeEntity(
                                    fromId = fromId,
                                    toId = toId,
                                    weight = dist,
                                    oneway = false,
                                    highwayType = highwayType
                                )
                            )
                            edges.add(
                                EdgeEntity(
                                    fromId = toId,
                                    toId = fromId,
                                    weight = dist,
                                    oneway = false,
                                    highwayType = highwayType
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

