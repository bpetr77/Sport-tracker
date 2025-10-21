package hu.bme.aut.android.sporttracker.data.local.graph.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "edges",
    indices = [
        Index(value = ["fromId"]),
        Index(value = ["toId"])
    ])
data class EdgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromId: Long,
    val toId: Long,
    val weight: Double,
    val oneway: Boolean,
    val highwayType: String? = null
)
