package hu.bme.aut.android.sporttracker.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tours ADD COLUMN comment TEXT")
        database.execSQL("ALTER TABLE tours ADD COLUMN weatherCondition TEXT")
    }
}

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        instance ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "sport_tracker_db"
        )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build().also { instance = it }
}

