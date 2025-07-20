package hu.bme.aut.android.sporttracker.data.tour.database

import android.content.Context
import androidx.room.Room
import hu.bme.aut.android.sporttracker.data.tour.database.AppDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add missing column: comment (nullable TEXT)
        database.execSQL("ALTER TABLE tours ADD COLUMN comment TEXT")
        // Add missing column: weatherCondition (nullable TEXT)
        database.execSQL("ALTER TABLE tours ADD COLUMN weatherCondition TEXT")
    }
}

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase2(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sport_tracker_db"
            ).build()
        }
        return instance!!
    }

    fun getDatabase(context: Context): AppDatabase =
        instance ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "sport_tracker_db"
        )
            .addMigrations(MIGRATION_1_2)
            // .fallbackToDestructiveMigration()
            .build().also { instance = it }
}

