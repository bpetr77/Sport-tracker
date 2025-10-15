package hu.bme.aut.android.sporttracker.data.local.graph.database

import android.content.Context
import androidx.room.Room

object GraphDatabaseProvider {
    @Volatile
    private var INSTANCE: GraphDatabase? = null

    fun getDatabase(context: Context): GraphDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                GraphDatabase::class.java,
                "graph_database"
            ).fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}
