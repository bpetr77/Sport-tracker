package hu.bme.aut.android.sporttracker.domain.repository

import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity

interface TourRepository {
    suspend fun addTour(tour: TourEntity)

    suspend fun getUserTours(userId: String): List<TourEntity>

    suspend fun getTourById(id: Long): TourEntity?

    suspend fun updateTour(tour: TourEntity)

    suspend fun deleteAllTours()

    suspend fun deleteTourById(id: Long)
}