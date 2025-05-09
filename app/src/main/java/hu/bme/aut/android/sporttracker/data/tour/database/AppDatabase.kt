package hu.bme.aut.android.sporttracker.data.tour.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.android.sporttracker.data.tour.database.TourDao
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.tour.database.Converters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TourEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tourDao(): TourDao
}




