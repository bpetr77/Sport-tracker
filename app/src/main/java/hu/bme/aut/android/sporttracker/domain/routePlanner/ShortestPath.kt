package hu.bme.aut.android.sporttracker.domain.routePlanner

import android.util.Log
import hu.bme.aut.android.sporttracker.data.routePlanner.model.Graph
import kotlin.math.*

class ShortestPath(private val graph: Graph) {

    /**
     * Dijkstra-algoritmus a legrövidebb út kiszámítására a Graph-ban.
     * A Graph-od adjacencyMap() függvényét feltételezi:
     *   Map<Long, List<Pair<Long, Double>>> = nodeId -> [(szomszédId, weight)]
     */
    fun findShortestPathIds(startId: Long, goalId: Long): List<Long> {
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
     * Node ID lista -> (lat, lon) koordinátalistává alakítás.
     */
    fun idsToLatLon(pathIds: List<Long>): List<Pair<Double, Double>> {
        val nodes = graph.allNodes()
        return pathIds.mapNotNull { id ->
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

    private fun planarDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dx = (lon2 - lon1) * 111320.0 * cos(Math.toRadians((lat1 + lat2) / 2.0))
        val dy = (lat2 - lat1) * 110540.0
        return sqrt(dx * dx + dy * dy)
    }
}
