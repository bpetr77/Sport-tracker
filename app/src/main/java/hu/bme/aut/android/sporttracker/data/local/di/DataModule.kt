package hu.bme.aut.android.sporttracker.data.local.di

import android.content.Context
import hu.bme.aut.android.sporttracker.data.local.database.AppDatabase
import hu.bme.aut.android.sporttracker.data.local.database.TourDao

fun provideTourDao(context: Context): TourDao {
    val db = AppDatabase.getDatabase(context)
    return db.tourDao()
}