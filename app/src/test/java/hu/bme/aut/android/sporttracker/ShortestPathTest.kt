package hu.bme.aut.android.sporttracker

import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Node
import hu.bme.aut.android.sporttracker.domain.routePlanner.ShortestPath
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ShortestPathTest {

    private lateinit var graph: Graph
    private lateinit var shortestPath: ShortestPath

    @Before
    fun setup() {
        graph = Graph()
        shortestPath = ShortestPath(graph)
    }

    @Test
    fun `findShortestPathIds finds direct path in simple line`() {
        // Arrange: A -> B -> C line
        val idA = 1L; val idB = 2L; val idC = 3L

        // Adding nodes (manually injecting into the graph for the test)
        // Reflection or a public setter might be needed if 'nodes' is private,
        // but we assume helper methods for the Graph class.
        addNodeToGraph(idA, 0.0, 0.0)
        addNodeToGraph(idB, 0.0, 0.1) // 0.1 degrees away
        addNodeToGraph(idC, 0.0, 0.2)

        // Adding edges: A->B and B->C
        addEdge(idA, idB, 10.0)
        addEdge(idB, idC, 10.0)

        // Act
        val result = shortestPath.findShortestPathIds(idA, idC)

        // Assert
        assertEquals(3, result.pathIds.size) // A, B, C
        assertEquals(listOf(idA, idB, idC), result.pathIds)
    }

    @Test
    fun `findShortestPathIds calculates correct physical distance along path`() {
        // Arrange
        val idA = 1L; val idB = 2L; val idC = 3L

        // A -> B -> C line
        val nodeA = Node(0.0, 0.0)
        val nodeB = Node(0.0, 0.1)
        val nodeC = Node(0.0, 0.2)

        addNodeToGraph(idA, nodeA)
        addNodeToGraph(idB, nodeB)
        addNodeToGraph(idC, nodeC)

        // Adding edges.
        addEdge(idA, idB, 10.0)
        addEdge(idB, idC, 10.0)

        // Act
        val result = shortestPath.findShortestPathIds(idA, idC)

        // Assert
        assertEquals(3, result.pathIds.size)
        assertEquals(listOf(idA, idB, idC), result.pathIds)

        // We calculate the actual distance based on the coordinates
        val distAB = testHaversine(nodeA.lat, nodeA.lon, nodeB.lat, nodeB.lon)
        val distBC = testHaversine(nodeB.lat, nodeB.lon, nodeC.lat, nodeC.lon)
        val expectedDistance = distAB + distBC

        // Verify if our function calculated the same.
        // (Using a small delta due to floating-point inaccuracy)
        assertEquals(expectedDistance, result.totalDistance, 0.001)
    }


    @Test
    fun `findShortestPathIds returns empty if no path exists`() {
        // Arrange: A and B are islands, no connection
        val idA = 1L; val idB = 2L
        addNodeToGraph(idA, 0.0, 0.0)
        addNodeToGraph(idB, 1.0, 1.0)

        // No addEdge call!

        // Act
        val result = shortestPath.findShortestPathIds(idA, idB)

        // Assert
        assertTrue(result.pathIds.isEmpty())
        assertEquals(Double.POSITIVE_INFINITY, result.totalDistance, 0.0)
    }

    @Test
    fun `findShortestPathIds chooses path with lower weight even if it visits more nodes`() {
        // Arrange
        val idA = 1L; val idB = 2L; val idC = 3L; val idD = 4L; val idE = 5L

        val baseLat = 47.0
        val baseLon = 19.0
        val step = 0.00001

        val nodeA = Node(baseLat, baseLon)
        val nodeB = Node(baseLat + step, baseLon + step)
        val nodeD = Node(baseLat + 2*step, baseLon + 2*step)

        val nodeC = Node(baseLat + 0.5*step, baseLon + 2*step)
        val nodeE = Node(baseLat + 1.5*step, baseLon + 2*step)

        addNodeToGraph(idA, nodeA)
        addNodeToGraph(idB, nodeB)
        addNodeToGraph(idC, nodeC)
        addNodeToGraph(idD, nodeD)
        addNodeToGraph(idE, nodeE)

        // Path 1  A -> B -> D

        addEdge(idA, idB, 50.0)
        addEdge(idB, idD, 50.0)

        // Path 2  A -> C -> E -> D
        addEdge(idA, idC, 5.0)
        addEdge(idC, idE, 5.0)
        addEdge(idE, idD, 5.0)

        // Act
        val result = shortestPath.findShortestPathIds(idA, idD)

        // Assert
        val expectedPath = listOf(idA, idC, idE, idD)

        assertEquals("Algorithm should choose the path with lowest total weight (15 vs 100)",
            expectedPath, result.pathIds)

    }

    // --- Helper functions ---

    private fun addNodeToGraph(id: Long, node: Node) {
        // Using reflection to access private/internal fields for testing purposes
        val nodesField = Graph::class.java.getDeclaredField("nodes")
        nodesField.isAccessible = true
        val nodesMap = nodesField.get(graph) as MutableMap<Long, Node>
        nodesMap[id] = node
    }

    private fun addEdge(from: Long, to: Long, weight: Double) {
        // Using reflection to inject edges into the private adjacency map
        val adjField = Graph::class.java.getDeclaredField("adjacency")
        adjField.isAccessible = true
        val adjMap = adjField.get(graph) as MutableMap<Long, MutableList<Pair<Long, Double>>>
        adjMap.computeIfAbsent(from) { mutableListOf() }.add(to to weight)
    }

    // Same math as in the production code to verify the result
    private fun testHaversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rLat1) * Math.cos(rLat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    private fun addNodeToGraph(id: Long, lat: Double, lon: Double) {
        // Since 'nodes' and 'adjacency' in your Graph class are likely private or internal,
        // we use either public methods or reflection for testing.
        // Here I assume there is an 'addNode' or similar testable method,
        // OR the test is in the same package as the Graph class.

        // Example for reflection, if absolutely necessary:
        val nodesField = Graph::class.java.getDeclaredField("nodes")
        nodesField.isAccessible = true
        val nodesMap = nodesField.get(graph) as MutableMap<Long, Node>
        nodesMap[id] = Node(lat, lon)
    }
}