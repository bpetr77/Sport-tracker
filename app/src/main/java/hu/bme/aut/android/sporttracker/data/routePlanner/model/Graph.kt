package hu.bme.aut.android.sporttracker.data.routePlanner.model

import hu.bme.aut.android.sporttracker.data.local.graph.model.EdgeEntity
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * Directed Graph implementation that:
 *  - deduplicates coordinates -> assigns Long nodeIds
 *  - stores nodes map: id -> Node(lat,lon)
 *  - stores adjacency: fromId -> list of (toId, weight)
 *  - supports building from RouteSegment list (respects "o" oneway flags)
 */
class Graph {
    // Node id -> Node (coords)
    val nodes: MutableMap<Long, Node> = mutableMapOf()

    // adjacency list: fromId -> list of Pairs(toId, weight)
    private val adjacency: MutableMap<Long, MutableList<Pair<Long, Double>>> = mutableMapOf()

    // coordinate -> nodeId (deduplication). Round coords to avoid tiny FP mismatches.
    private val coordToId: MutableMap<Pair<Long, Long>, Long> = mutableMapOf()

    private var nextId = 1L

    // Precision used for deduplication: multiply degrees by this and store as Long
    // ~1e6 -> ~0.11 m precision in lat/lon; pick as required (1e6 is typical)
    private val precisionMultiplier = 1_000_000L

    // --- Helper: normalize a coordinate into a pair of Longs for stable hashing ---
    private fun keyFor(lat: Double, lon: Double): Pair<Long, Long> {
        val latKey = (lat * precisionMultiplier).roundToLong()
        val lonKey = (lon * precisionMultiplier).roundToLong()
        return Pair(latKey, lonKey)
    }

    /** Create or return existing node id for given coordinates */
    fun getOrCreateNodeId(lat: Double, lon: Double): Long {
        val key = keyFor(lat, lon)
        val existing = coordToId[key]
        if (existing != null) return existing
        val id = nextId++
        coordToId[key] = id
        nodes[id] = Node(lat, lon)
        adjacency.computeIfAbsent(id) { mutableListOf() }
        return id
    }

    /** Return Node by id */
    fun getNode(id: Long): Node? = nodes[id]

    /** Add a directed edge from fromId -> toId with given weight (meters). */
    fun addDirectedEdge(fromId: Long, toId: Long, weight: Double) {
        adjacency.computeIfAbsent(fromId) { mutableListOf() }.add(Pair(toId, weight))
    }

    fun buildFromEntities2(nodes: List<NodeEntity>, edges: List<EdgeEntity>) {
        // Nodes létrehozása
        nodes.forEach { getOrCreateNodeId(it.lat, it.lon) }

        // Edge-ek létrehozása a DB-ben tárolt oneway flag alapján
        edges.forEach { edge ->
            addDirectedEdge(edge.fromId, edge.toId, edge.weight)
            if (!edge.oneway) {
                // kétirányú: add visszafelé is
                addDirectedEdge(edge.toId, edge.fromId, edge.weight)
            }
        }
    }

    // TODO: ADJUST THE WEIGHTS AND FIND THE COMBINATIONS BETWEEN ROADS AND CYCLELANSES
    fun adjustedWeight(edge: EdgeEntity): Double {
        var w = edge.weight
        w *= when (edge.cycleLane) {
            "track" -> 0.2
            "lane" -> 0.4
            "shared_lane" -> 0.8
            "shared_busway" -> 0.6
            "crossing" -> 0.9
            "opposite_lane" -> 0.9
            "opposite" -> 0.9
            "designated" -> 0.45
            "traffic_island" -> 1.1
            "no" -> 1.0
            "proposed" -> 1.5
            null -> 1.0
            else -> 1.0
        }

        w *= when (edge.highwayType) {
            "cycleway" -> 0.1
            "motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link", "bus_guideway", "escape", "road" -> 100.0 // bringával tiltott vagy veszélyes
            "secondary", "secondary_link", "busway", "bridleway", "steps", "corridor", "path" -> 1.8
            "tertiary", "tertiary_link", "footway", "track" -> 1.4
            "residential", "living_street", "unclassified" -> 0.8
            "service", "pedestrian" -> 1.1
            "construction", "proposed" -> 5.0
            "platform", "bus_stop" -> 2.0
            else -> 1.0
        }

        return w
    }
    fun buildFromEntities(nodes: List<NodeEntity>, edges: List<EdgeEntity>) {
        // Töltsd be a node-okat az adatbázisban lévő ID-kkal
        nodes.forEach { node ->
            this.nodes[node.id] = Node(node.lat, node.lon)
            adjacency.computeIfAbsent(node.id) { mutableListOf() }
        }

        // Az élek összekötése a már ismert ID-k alapján
        edges.forEach { edge ->
            addDirectedEdge(edge.fromId, edge.toId, adjustedWeight(edge))
            if (!edge.oneway) {
                addDirectedEdge(edge.toId, edge.fromId, adjustedWeight(edge))
            }
        }
    }
    /** Add edge using Node coordinates; optionally bidirectional. */
    fun addEdge(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        weight: Double,
        bidirectional: Boolean = true
    ) {
        val fromId = getOrCreateNodeId(fromLat, fromLon)
        val toId = getOrCreateNodeId(toLat, toLon)
        addDirectedEdge(fromId, toId, weight)
        if (bidirectional) {
            addDirectedEdge(toId, fromId, weight)
        }
    }

    /** Convenience: get neighbors as Edge objects for a Node (lat/lon deduped) */
    fun neighborsOf(node: Node): List<Edge> {
        val id = coordToId[keyFor(node.lat, node.lon)] ?: return emptyList()
        return adjacency[id]?.mapNotNull { (toId, w) ->
            nodes[toId]?.let { Edge(it, w) }
        } ?: emptyList()
    }

    /** Get all nodes (id -> Node) - read-only view */
    fun allNodes(): Map<Long, Node> = nodes.toMap()

    /** Get adjacency map (read-only copy) as id -> list of (toId, weight) */
    fun adjacencyMap(): Map<Long, List<Pair<Long, Double>>> =
        adjacency.mapValues { it.value.toList() }

    /** Build graph from RouteSegment list and honnor 'o' flag:
     *  o == "y"  -> one-way following coordinates order (only forward)
     *  o == "-"  -> one-way reverse of coordinates order (only backward)
     *  o == null -> bidirectional (default)
     */
    fun buildFromSegments(segments: List<RouteSegment>) {
        // reset
        nodes.clear()
        adjacency.clear()
        coordToId.clear()
        nextId = 1L
        var count = 0
        for (seg in segments) {
            if (count > 1000) break
            val coords = seg.c
            if (coords.size < 2) continue
            count++
            // interpret oneway flag (our compact format)
            val oneway = seg.o
            val forwardOneWay = (oneway == "y" || oneway == "yes")
            val reverseOneWay = (oneway == "-" || oneway == "-1")

            // iterate consecutive pairs
            for (i in 0 until coords.size - 1) {
                val (latA, lonA) = Pair(coords[i][0], coords[i][1])
                val (latB, lonB) = Pair(coords[i + 1][0], coords[i + 1][1])
                val dist = planarDistance(latA, lonA, latB, lonB)

                when {
                    forwardOneWay -> {
                        // only A -> B
                        addEdge(latA, lonA, latB, lonB, dist, bidirectional = false)
                    }

                    reverseOneWay -> {
                        // only B -> A (reverse)
                        addEdge(latB, lonB, latA, lonA, dist, bidirectional = false)
                    }

                    else -> {
                        // default: two-way
                        addEdge(latA, lonA, latB, lonB, dist, bidirectional = true)
                    }
                }
            }
        }
    }

    fun planarDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dx = (lon2 - lon1) * 111_320 * cos(Math.toRadians(lat1))
        val dy = (lat2 - lat1) * 110_540
        return sqrt(dx * dx + dy * dy)
    }

    companion object
}