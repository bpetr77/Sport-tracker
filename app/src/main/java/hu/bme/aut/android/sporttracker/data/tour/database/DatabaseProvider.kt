package hu.bme.aut.android.sporttracker.data.tour.database

import android.content.Context
import androidx.room.Room
import hu.bme.aut.android.sporttracker.data.tour.database.AppDatabase

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sport_tracker_db"
            ).build()
        }
        return instance!!
    }
}