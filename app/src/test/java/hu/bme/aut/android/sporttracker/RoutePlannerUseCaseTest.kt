package hu.bme.aut.android.sporttracker

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.domain.usecase.RoutePlannerUseCase
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class RoutePlannerUseCaseTest {

    private val useCase = RoutePlannerUseCase()

    // Segédfüggvény a lebegőpontos összehasonlításhoz
    private fun assertCoordinatesClose(expected: LatLng, actual: LatLng, delta: Double = 0.0001) {
        assertEquals("Latitude mismatch", expected.latitude, actual.latitude, delta)
        assertEquals("Longitude mismatch", expected.longitude, actual.longitude, delta)
    }

    @Test
    fun `calculateBoundingBox creates small box for short distance`() {
        // Arrange: Két nagyon közeli pont (pl. 100 méterre)
        val from = LatLng(47.4979, 19.0402) // Deák tér környéke
        val to = LatLng(47.4988, 19.0402)   // Kicsit északabbra

        // Act
        val bbox = useCase.calculateBoundingBox(from, to)

        // Assert
        // Rövid távnál (<= 500m) a doboz négyzetes és bővebb (2x távolság)
        // Ellenőrizzük, hogy a sarkok nem üresek
        assertTrue(bbox.corners.isNotEmpty())
        assertEquals(4, bbox.corners.size)

        // Ellenőrizzük a min/max értékeket
        assertTrue(bbox.minLat < from.latitude)
        assertTrue(bbox.maxLat > to.latitude)
    }

    @Test
    fun `calculateBoundingBox rotates correctly for diagonal route`() {
        // Arrange: Átlós út (Dél-Nyugat -> Észak-Kelet)
        val from = LatLng(47.0, 19.0)
        val to = LatLng(47.1, 19.1)

        // Act
        val bbox = useCase.calculateBoundingBox(from, to)

        // Assert
        // Forgatásnál a min/max lat/lon-nak tartalmaznia kell mindkét pontot
        assertTrue(bbox.minLat <= from.latitude)
        assertTrue(bbox.maxLat >= to.latitude)
        assertTrue(bbox.minLon <= from.longitude)
        assertTrue(bbox.maxLon >= to.longitude)
    }

    @Test
    fun `haversine distance logic via bounding box dimensions`() {
        // Mivel a haversine private, a bounding box méretein keresztül teszteljük indirekt módon.
        // Hosszú táv (> 4000m) esetén a doboz szűkebb (1.1x hossz, 0.5x szélesség)

        val from = LatLng(47.4979, 19.0402)
        val to = LatLng(47.5500, 19.0402) // Kb. 5-6 km északra

        val bbox = useCase.calculateBoundingBox(from, to)

        val heightLat = bbox.maxLat - bbox.minLat
        val widthLon = bbox.maxLon - bbox.minLon

        // A doboznak magasabbnak kell lennie mint amilyen széles (mivel Észak felé megyünk)
        assertTrue("Height should be significantly larger than width for N-S route", heightLat > widthLon)
    }
}