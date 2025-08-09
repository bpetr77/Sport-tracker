package hu.bme.aut.android.sporttracker.data.tour.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity

@Database(entities = [TourEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tourDao(): TourDao
}




