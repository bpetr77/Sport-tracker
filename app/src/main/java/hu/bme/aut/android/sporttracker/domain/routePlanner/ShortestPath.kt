package hu.bme.aut.android.sporttracker.domain.routePlanner

import android.util.Log
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Node
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.PathResult
import kotlin.math.*

class ShortestPath(private val graph: Graph) {
    /**
     * A*-algoritmus a legrövidebb út kiszámítására a Graph-ban.
     */
//    fun findShortestPathIds(startId: Long, goalId: Long): PathResult {
//        val adjacency = graph.adjacencyMap()
//
//        val nodes = graph.nodes
//
//        val startNode = nodes[startId]
//        val goalNode = nodes[goalId]
//
//        if (startNode == null || goalNode == null) {
//            Log.e("AStar", "Missing node(s): start=$startNode, goal=$goalNode, ids=($startId,$goalId)")
//            return PathResult(emptyList(), Double.POSITIVE_INFINITY)
//        }
//
//
//       if (startId == goalId) return PathResult(listOf(startId), 0.0)
//
//        val gScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
//        val fScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
//        val prev = mutableMapOf<Long, Long?>()
//        val openSet = java.util.PriorityQueue(compareBy<Pair<Long, Double>> { it.second })
//
//        gScore[startId] = 0.0
//        fScore[startId] = heuristicDistance(startNode, goalNode)
//        openSet.add(startId to fScore[startId]!!)
//
//        while (openSet.isNotEmpty()) {
//            val (currentId, _) = openSet.poll()
//
//            if (currentId == goalId) {
//                val path = mutableListOf<Long>()
//                var cur: Long? = goalId
//                while (cur != null) {
//                    path.add(cur)
//                    cur = prev[cur]
//                }
//                path.reverse()
//                var totalDistance = 0.0
//                for (i in 0 until path.size - 1) {
//                    val nodeA = nodes[path[i]]!!
//                    val nodeB = nodes[path[i+1]]!!
//                    totalDistance += heuristicDistance(nodeA, nodeB)
//                }
//                //Log.d("AStar", "Path found: length=${path.size}, distance=$totalDistance")
//                return PathResult(path, totalDistance)
//            }
//
//            for ((neighborId, weight) in adjacency[currentId] ?: emptyList()) {
//                val neighborNode = nodes[neighborId] ?: continue
//
//                val tentativeG = gScore.getValue(currentId) + weight
//                if (tentativeG < gScore.getValue(neighborId)) {
//                    prev[neighborId] = currentId
//                    gScore[neighborId] = tentativeG
//                    fScore[neighborId] = tentativeG + heuristicDistance(neighborNode, goalNode)
//                    openSet.add(neighborId to fScore[neighborId]!!)
//                }
//            }
//        }
//
//        //Log.w("AStar", "No path found from $startId to $goalId")
//        return PathResult(emptyList(), Double.POSITIVE_INFINITY)
//    }
    fun findShortestPathIds(startId: Long, goalId: Long): PathResult {
        val adjacency = graph.adjacencyMap()
        val nodes = graph.nodes

        val startNode = nodes[startId]
        val goalNode = nodes[goalId]

        if (startNode == null || goalNode == null) {
            return PathResult(emptyList(), Double.POSITIVE_INFINITY, emptyList())
        }

        if (startId == goalId) return PathResult(listOf(startId), 0.0, emptyList())

        // --- A* Inicializálás ---
        val gScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
        val fScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
        val prev = mutableMapOf<Long, Long?>()
        val openSet = java.util.PriorityQueue(compareBy<Pair<Long, Double>> { it.second })

        gScore[startId] = 0.0
        fScore[startId] = heuristicDistance(startNode, goalNode)
        openSet.add(startId to fScore[startId]!!)

        while (openSet.isNotEmpty()) {
            val (currentId, _) = openSet.poll()

            // --- CÉL ELÉRVE: Útvonal és Típusok rekonstrukciója ---
            if (currentId == goalId) {
                val path = mutableListOf<Long>()
                var cur: Long? = goalId
                while (cur != null) {
                    path.add(cur)
                    cur = prev[cur]
                }
                path.reverse()

                // Út típusok és valódi távolság kigyűjtése
                val types = mutableListOf<String>()
                var totalDistance = 0.0

                for (i in 0 until path.size - 1) {
                    val fromId = path[i]
                    val toId = path[i + 1]

                    // Megkeressük az élt a fromId szomszédjai között
                    // Most már GraphEdge objektumokat kapunk vissza
                    val edge = adjacency[fromId]?.find { it.toId == toId }

                    if (edge != null) {
                        types.add(edge.type) // Típus mentése

                        val nodeA = nodes[fromId]!!
                        val nodeB = nodes[toId]!!
                        totalDistance += heuristicDistance(nodeA, nodeB)
                    } else {
                        types.add("unknown")
                    }
                }

                return PathResult(path, totalDistance, types)
            }

            // --- SZOMSZÉDOK BEJÁRÁSA ---
            // Itt 'edge' most már egy GraphEdge objektum
            for (edge in adjacency[currentId] ?: emptyList()) {
                val neighborId = edge.toId
                val weight = edge.weight

                val neighborNode = nodes[neighborId] ?: continue

                val tentativeG = gScore.getValue(currentId) + weight
                if (tentativeG < gScore.getValue(neighborId)) {
                    prev[neighborId] = currentId
                    gScore[neighborId] = tentativeG
                    fScore[neighborId] = tentativeG + heuristicDistance(neighborNode, goalNode)
                    openSet.add(neighborId to fScore[neighborId]!!)
                }
            }
        }

        return PathResult(emptyList(), Double.POSITIVE_INFINITY, emptyList())
    }
    private fun heuristicDistance(a: Node, b: Node): Double {
        val R = 6371000.0 // Föld sugara méterben
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLon = Math.toRadians(b.lon - a.lon)
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)

        val h = sin(dLat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(h), sqrt(1 - h))
        return R * c
    }

    /**
     * Node ID lista -> (lat, lon) koordinátalistává alakítás.
     */
    fun idsToLatLon(pathIds: PathResult): List<Pair<Double, Double>> {
        val nodes = graph.allNodes()
        return pathIds.pathIds.mapNotNull { id ->
            nodes[id]?.let { node -> node.lat to node.lon }
        }
    }

    /**
     * Legközelebbi csomópont keresése egy tetszőleges koordinátához.
     * Egyszerű (O(n)) változat, később lehet KD-tree-vel gyorsítani.
     */
    fun findNearestNodeId(lat: Double, lon: Double): Long? {
        var bestId: Long? = null
        var bestDist = Double.POSITIVE_INFINITY
        for ((id, node) in graph.allNodes()) {
            val d = planarDistanceMeters(lat, lon, node.lat, node.lon)
            if (d < bestDist) {
                bestDist = d
                bestId = id
            }
        }
        return bestId
    }

    // TODO: same function as heuristicDistance
    private fun planarDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dx = (lon2 - lon1) * 111320.0 * cos(Math.toRadians((lat1 + lat2) / 2.0))
        val dy = (lat2 - lat1) * 110540.0
        return sqrt(dx * dx + dy * dy)
    }
}
