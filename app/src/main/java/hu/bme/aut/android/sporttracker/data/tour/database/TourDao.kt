package hu.bme.aut.android.sporttracker.data.tour.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.sporttracker.data.tour.model.TourEntity

@Dao
interface TourDao {

    @Query("SELECT * FROM tours WHERE id = :id")
    suspend fun getTourById(id: Long): TourEntity?

    @Query("SELECT * FROM tours")
    suspend fun getAllTours(): List<TourEntity>

    @Insert
    suspend fun insertTour(tour: TourEntity): Long

    @Update
    suspend fun updateTour(tour: TourEntity)

    @Query("DELETE FROM tours WHERE id = :id")
    suspend fun deleteTourById(id: Long)
}