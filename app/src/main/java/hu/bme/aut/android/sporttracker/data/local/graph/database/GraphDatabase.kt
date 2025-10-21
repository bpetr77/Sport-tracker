package hu.bme.aut.android.sporttracker.data.local.graph.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.bme.aut.android.sporttracker.data.local.graph.model.EdgeEntity
import hu.bme.aut.android.sporttracker.data.local.graph.model.NodeEntity

@Database(entities = [NodeEntity::class, EdgeEntity::class], version = 3)
abstract class GraphDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
    abstract fun edgeDao(): EdgeDao
}
