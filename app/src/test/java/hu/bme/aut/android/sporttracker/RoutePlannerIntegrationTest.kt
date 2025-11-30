package hu.bme.aut.android.sporttracker

import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Node
import hu.bme.aut.android.sporttracker.domain.routePlanner.ShortestPath
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RoutePlannerIntegrationTest {

    private lateinit var graph: Graph
    private lateinit var shortestPath: ShortestPath

    // Simulating a small 3x2 grid road network
    // ID Allocation:
    // 1(Start) --- 2 --- 3(Goal)   <-- Main Road (Fast, Low Weight)
    // |            |     |
    // 4 ---------- 5 --- 6         <-- Side Street (Slow, High Weight)

    @Before
    fun setup() {
        graph = Graph()
        shortestPath = ShortestPath(graph)
        buildCityNetwork()
    }

    @Test
    fun `integration_shouldPreferMainRoad_WhenNoObstacles`() {
        // Arrange
        val startId = 1L
        val goalId = 3L

        // Act
        val result = shortestPath.findShortestPathIds(startId, goalId)

        // Assert
        // The fastest path is the Main Road: 1 -> 2 -> 3
        val expectedPath = listOf(1L, 2L, 3L)

        assertEquals("The algorithm should choose the fast main road (1-2-3)",
            expectedPath, result.pathIds)
    }

    @Test
    fun `integration_shouldRerouteViaSideStreets_WhenMainRoadIsBlocked`() {
        // Arrange
        val startId = 1L
        val goalId = 3L

        // SIMULATION: Road construction/Blockage between Node 2 and Node 3!
        // We simulate this by assigning a massive weight to the edge.
        // Since there is no direct 'removeEdge' method exposed, we manually update the weight to "infinity".
        updateEdgeWeight(from = 2L, to = 3L, newWeight = 10000.0)

        // Act
        val result = shortestPath.findShortestPathIds(startId, goalId)

        // Assert
        // It can no longer go 1->2->3 because the 2->3 section is blocked.
        // It must detour: 1 -> 4 -> 5 -> 6 -> 3 (or a similar alternative through side streets).
        // The key check is that it DOES NOT return the original main road path.
        assertNotEquals("Should not use the blocked main road", listOf(1L, 2L, 3L), result.pathIds)

        // Verify that a valid path was found (result is not empty)
        assertTrue("It should find an alternative route", result.pathIds.isNotEmpty())
        assertEquals(startId, result.pathIds.first())
        assertEquals(goalId, result.pathIds.last())
    }

    // --- Helper: City Network Construction Logic ---

    private fun buildCityNetwork() {
        // 1. Creating Nodes
        // Creating a grid based on coordinates
        val baseLat = 47.5
        val baseLon = 19.0
        val step = 0.001 // Approx. 100 meters distance

        // Main Road Line (North)
        addNodeInternal(1L, baseLat, baseLon)           // Start
        addNodeInternal(2L, baseLat, baseLon + step)
        addNodeInternal(3L, baseLat, baseLon + 2*step)  // Goal

        // Side Street Line (South)
        addNodeInternal(4L, baseLat - step, baseLon)
        addNodeInternal(5L, baseLat - step, baseLon + step)
        addNodeInternal(6L, baseLat - step, baseLon + 2*step)

        // 2. Connecting Edges (Roads)

        // MAIN ROAD: Very low weight (Fast)
        // 1 -> 2 -> 3
        addEdgeInternal(1L, 2L, weight = 1.0)
        addEdgeInternal(2L, 3L, weight = 1.0)

        // SIDE STREETS: High weight (Slow, many turns)
        // 4 -> 5 -> 6
        addEdgeInternal(4L, 5L, weight = 10.0)
        addEdgeInternal(5L, 6L, weight = 10.0)

        // CROSS STREETS: Connecting Main Road to Side Streets
        // 1-4, 2-5, 3-6
        addEdgeInternal(1L, 4L, weight = 5.0)
        addEdgeInternal(2L, 5L, weight = 5.0)
        addEdgeInternal(3L, 6L, weight = 5.0)

        // Reverse directions (Simulating two-way streets)
        addEdgeInternal(4L, 1L, weight = 5.0)
        addEdgeInternal(5L, 2L, weight = 5.0)
        addEdgeInternal(6L, 3L, weight = 5.0)

        // Connect Node 6 back to Node 3 (so the detour can reach the goal)
        addEdgeInternal(6L, 3L, weight = 5.0)
    }

    // Reflection helpers to avoid opening production code to "public" visibility
    // These allow the test to inject data into private maps of the Graph class.
    private fun addNodeInternal(id: Long, lat: Double, lon: Double) {
        val nodesField = Graph::class.java.getDeclaredField("nodes")
        nodesField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val nodesMap = nodesField.get(graph) as MutableMap<Long, Node>
        nodesMap[id] = Node(lat, lon)
    }

    private fun addEdgeInternal(from: Long, to: Long, weight: Double) {
        val adjField = Graph::class.java.getDeclaredField("adjacency")
        adjField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val adjMap = adjField.get(graph) as MutableMap<Long, MutableList<Pair<Long, Double>>>
        adjMap.computeIfAbsent(from) { mutableListOf() }.add(to to weight)
    }

    private fun updateEdgeWeight(from: Long, to: Long, newWeight: Double) {
        // Helper to find a specific edge in the list and update its weight
        val adjField = Graph::class.java.getDeclaredField("adjacency")
        adjField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val adjMap = adjField.get(graph) as MutableMap<Long, MutableList<Pair<Long, Double>>>

        val edges = adjMap[from]
        if (edges != null) {
            // Remove the old edge
            edges.removeIf { it.first == to }
            // Add the new edge with updated weight
            edges.add(to to newWeight)
        }
    }
}