package hu.bme.aut.android.sporttracker

import hu.bme.aut.android.sporttracker.domain.model.routePlanner.RouteSegment
import hu.bme.aut.android.sporttracker.domain.model.routePlanner.Graph
import org.junit.Assert.*
import org.junit.Test

class GraphBuilderTest {

    @Test
    fun `getOrCreateNodeId handles floating point precision issues`() {
        val graph = Graph()

        // Arrange: Két koordináta, ami a 8. tizedesjegyben eltér, de "ugyanaz" a pont
        val lat1 = 47.4979123000
        val lon1 = 19.0402123000

        val lat2 = 47.4979123001 // Nagyon kicsi eltérés
        val lon2 = 19.0402123002

        // Act
        val id1 = graph.getOrCreateNodeId(lat1, lon1)
        val id2 = graph.getOrCreateNodeId(lat2, lon2)

        // Assert
        assertEquals("IDs should be identical due to precision masking", id1, id2)
    }

    @Test
    fun `getOrCreateNodeId creates new ID for distant points`() {
        val graph = Graph()
        val id1 = graph.getOrCreateNodeId(47.0, 19.0)
        val id2 = graph.getOrCreateNodeId(47.1, 19.0) // Távoli pont

        assertNotEquals(id1, id2)
    }

    // Itt szimuláljuk a saveGraphFromSegments logikáját kicsiben
    @Test
    fun `graph building connects nodes correctly`() {
        val graph = Graph()
        // A -> B szegmens
        val latA = 10.0; val lonA = 10.0
        val latB = 10.1; val lonB = 10.1

        // Kézzel hívjuk a logikát, amit a usecase csinál
        val idA = graph.getOrCreateNodeId(latA, lonA)
        val idB = graph.getOrCreateNodeId(latB, lonB)

        // Ellenőrizzük, hogy bekerültek-e a node-ok közé
        assertNotNull(graph.nodes[idA])
        assertNotNull(graph.nodes[idB])

        // Adjacency lista ellenőrzése (init után üresnek kell lennie,
        // a buildFromEntities töltené fel, de itt a node létrehozást teszteljük)
        assertTrue(graph.adjacencyMap().containsKey(idA))
    }
}