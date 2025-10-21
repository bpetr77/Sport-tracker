package hu.bme.aut.android.sporttracker.data.local.graph.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.bme.aut.android.sporttracker.data.local.graph.model.EdgeEntity

@Dao
interface EdgeDao {
    @Query("SELECT * FROM edges WHERE fromId IN (:nodeIds) OR toId IN (:nodeIds)")
    suspend fun getEdgesForNodes(nodeIds: List<Long>): List<EdgeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(edges: List<EdgeEntity>)

    @Query("DELETE FROM edges")
    suspend fun deleteAll()

    @Query("SELECT * FROM edges WHERE fromId IN (:nodeIds)")
    suspend fun getEdgesFromNodes(nodeIds: List<Long>): List<EdgeEntity>

    @Query("SELECT * FROM edges WHERE toId IN (:nodeIds)")
    suspend fun getEdgesToNodes(nodeIds: List<Long>): List<EdgeEntity>
}
