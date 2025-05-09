package hu.bme.aut.android.sporttracker.data.tour.repository

import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.tour.database.TourDao

//object TourRepository{
//    private val tours = mutableListOf<TourEntity>()
//
//    fun addTour(tour: TourEntity) {
//        tours.add(tour)
//    }
//
//    fun getTourById(id: Long): TourEntity? {
//        return tours.find { it.id == id }
//    }
//
//    fun getAllTours(): List<TourEntity> {
//        return tours.toList()
//    }
//
//    fun updateTour(tour: TourEntity) {
//        val index = tours.indexOfFirst { it.id == tour.id }
//        if (index != -1) {
//            tours[index] = tour
//        }
//    }
//
//    fun deleteTourById(id: Long) {
//        tours.removeAll { it.id == id }
//    }
//}

class TourRepository(private val tourDao: TourDao) {
    suspend fun addTour(tour: TourEntity) = tourDao.insertTour(tour)

    suspend fun getTourById(id: Long) = tourDao.getTourById(id)

    suspend fun getAllTours() = tourDao.getAllTours()

    suspend fun updateTour(tour: TourEntity) = tourDao.updateTour(tour)

    suspend fun deleteTourById(id: Long) = tourDao.deleteTourById(id)
}