package hu.bme.aut.android.sporttracker.data.local.graph.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity

@Dao
interface NodeDao {
    @Query("SELECT * FROM nodes WHERE lat BETWEEN :minLat AND :maxLat AND lon BETWEEN :minLon AND :maxLon")
    suspend fun getNodesInBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<NodeEntity>

    @Query("SELECT COUNT(*) FROM nodes")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<NodeEntity>)

    @Query("DELETE FROM nodes")
    suspend fun deleteAll()
}
