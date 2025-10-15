package hu.bme.aut.android.sporttracker.data.local.graph.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nodes")
data class NodeEntity(
    @PrimaryKey val id: Long,
    val lat: Double,
    val lon: Double
)
