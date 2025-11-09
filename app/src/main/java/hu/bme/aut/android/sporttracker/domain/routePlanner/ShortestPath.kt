package hu.bme.aut.android.sporttracker.domain.routePlanner

import android.util.Log
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Node
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.PathResult
import kotlin.math.*

class ShortestPath(private val graph: Graph) {

    /**
     * Dijkstra-algoritmus a legrövidebb út kiszámítására a Graph-ban.
     * A Graph-od adjacencyMap() függvényét feltételezi:
     *   Map<Long, List<Pair<Long, Double>>> = nodeId -> [(szomszédId, weight)]
     */
    fun findShortestPathIds2(startId: Long, goalId: Long): List<Long> {
        if (startId == goalId) return listOf(startId)

        val dist = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
        val prev = mutableMapOf<Long, Long?>()
        val visited = mutableSetOf<Long>()

        val pq = java.util.PriorityQueue(compareBy<Pair<Long, Double>> { it.second })
        dist[startId] = 0.0
        pq.add(startId to 0.0)

        val adjacency = graph.adjacencyMap()
        Log.d("Dijkstra", "Neighbors of start: ${graph.adjacencyMap()[startId]}")
        Log.d("Dijkstra", "Neighbors of goal: ${graph.adjacencyMap()[goalId]}")
        while (pq.isNotEmpty()) {
            val (u, dU) = pq.poll()
            if (visited.contains(u)) continue
            visited.add(u)

            if (u == goalId) break

            for ((v, w) in adjacency[u] ?: emptyList()) {
                val alt = dU + w
                if (alt < dist.getValue(v)) {
                    dist[v] = alt
                    prev[v] = u
                    pq.add(v to alt)
                }
            }
        }

        // nincs elérési út
        if (!prev.containsKey(goalId) && startId != goalId) return emptyList()

        // visszaépítés
        val path = mutableListOf<Long>()
        var cur: Long? = goalId
        while (cur != null) {
            path.add(cur)
            cur = prev[cur]
        }
        path.reverse()
        return path
    }

    /**
     * A*-algoritmus a legrövidebb út kiszámítására a Graph-ban.
     */
    fun findShortestPathIds(startId: Long, goalId: Long): PathResult {
        val adjacency = graph.adjacencyMap()
        Log.d("AStar", "1")

        val nodes = graph.nodes
        Log.d("AStar", "2")

        // Ellenőrzés: léteznek-e a start és goal csomópontok
        val startNode = nodes[startId]
        val goalNode = nodes[goalId]

        if (startNode == null || goalNode == null) {
            Log.e("AStar", "Missing node(s): start=$startNode, goal=$goalNode, ids=($startId,$goalId)")
            return PathResult(emptyList(), Double.POSITIVE_INFINITY)
        }

        if (startId == goalId) return PathResult(listOf(startId), 0.0)

        val gScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
        val fScore = mutableMapOf<Long, Double>().withDefault { Double.POSITIVE_INFINITY }
        val prev = mutableMapOf<Long, Long?>()
        val openSet = java.util.PriorityQueue(compareBy<Pair<Long, Double>> { it.second })

        gScore[startId] = 0.0
        fScore[startId] = heuristicDistance(startNode, goalNode)
        openSet.add(startId to fScore[startId]!!)
        Log.d("AStar", "3")

        while (openSet.isNotEmpty()) {
            val (currentId, _) = openSet.poll()

            if (currentId == goalId) {
                val path = mutableListOf<Long>()
                var cur: Long? = goalId
                while (cur != null) {
                    path.add(cur)
                    cur = prev[cur]
                }
                path.reverse()
                var totalDistance = 0.0
                for (i in 0 until path.size - 1) {
                    val nodeA = nodes[path[i]]!!
                    val nodeB = nodes[path[i+1]]!!
                    totalDistance += heuristicDistance(nodeA, nodeB)
                }
                Log.d("AStar", "Path found: length=${path.size}, distance=$totalDistance")
                return PathResult(path, totalDistance)
            }

            for ((neighborId, weight) in adjacency[currentId] ?: emptyList()) {
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

        Log.w("AStar", "No path found from $startId to $goalId")
        return PathResult(emptyList(), Double.POSITIVE_INFINITY)
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

//    private fun heuristicDistance2(a: Node, b: Node): Double {
//        val dx = (a.lon - b.lon) * 111320 * cos(Math.toRadians((a.lat + b.lat) / 2))
//        val dy = (a.lat - b.lat) * 110540
//        return sqrt(dx * dx + dy * dy)
//    }

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
