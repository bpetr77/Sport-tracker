package hu.bme.aut.android.sporttracker.data.local.tour.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity

@Dao
interface TourDao {
    @Query("SELECT * FROM tours WHERE id = :id")
    suspend fun getTourById(id: Long): TourEntity?

    @Query("SELECT * FROM tours")
    suspend fun getAllTours(): List<TourEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTour(tour: TourEntity): Long

    @Update
    suspend fun updateTour(tour: TourEntity)

    @Query("DELETE FROM tours WHERE id = :id")
    suspend fun deleteTourById(id: Long)

    @Query("DELETE FROM tours")
    suspend fun deleteAllTours()

    @Query("SELECT * FROM tours WHERE userId = :userId")
    suspend fun getToursByUser(userId: String): List<TourEntity>
}