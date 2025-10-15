package hu.bme.aut.android.sporttracker.data.local.tour.di

import android.content.Context
import hu.bme.aut.android.sporttracker.data.local.tour.database.AppDatabase
import hu.bme.aut.android.sporttracker.data.local.tour.database.TourDao

fun provideTourDao(context: Context): TourDao {
    val db = AppDatabase.getDatabase(context)
    return db.tourDao()
}