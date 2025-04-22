package hu.bme.aut.android.sporttracker.data.tour.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.android.sporttracker.data.tour.database.TourDao
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.tour.database.Converters

@Database(entities = [TourEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tourDao(): TourDao
}