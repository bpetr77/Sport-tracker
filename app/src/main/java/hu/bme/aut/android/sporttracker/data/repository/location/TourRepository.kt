package hu.bme.aut.android.sporttracker.data.repository.location

import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.local.tour.database.TourDao

class TourRepository(private val tourDao: TourDao) {
    suspend fun addTour(tour: TourEntity) = tourDao.insertTour(tour)

    suspend fun getTourById(id: Long) = tourDao.getTourById(id)

    suspend fun getAllTours() = tourDao.getAllTours()

    suspend fun deleteAllTours() = tourDao.deleteAllTours()

    suspend fun updateTour(tour: TourEntity) = tourDao.updateTour(tour)

    suspend fun deleteTourById(id: Long) = tourDao.deleteTourById(id)

    suspend fun getToursByUser(userId: String) = tourDao.getToursByUser(userId)
}