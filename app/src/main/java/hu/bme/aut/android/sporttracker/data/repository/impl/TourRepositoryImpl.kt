package hu.bme.aut.android.sporttracker.data.repository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.android.sporttracker.data.local.tour.database.TourDao
import hu.bme.aut.android.sporttracker.data.local.tour.model.TourEntity
import hu.bme.aut.android.sporttracker.data.mappers.toEntity
import hu.bme.aut.android.sporttracker.data.mappers.toFirebase
import hu.bme.aut.android.sporttracker.data.remote.firebase.model.FirebaseTour
import hu.bme.aut.android.sporttracker.domain.repository.TourRepository
import kotlinx.coroutines.tasks.await

class TourRepositoryImpl(
    private val dao: TourDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): TourRepository {
    override suspend fun addTour(tour: TourEntity) {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        val tourWithUser = tour.copy(userId = userId)
        dao.insertTour(tourWithUser)

        val firebaseTour = tour.toFirebase()

        try {
            firestore.collection("users")
                .document(userId)
                .collection("tours")
                .document(firebaseTour.id)
                .set(firebaseTour)
                .await()

        } catch (e: Exception) {
            Log.e("TourRepository", "Failed to save tour to Firestore", e)
        }
    }

    override suspend fun getUserTours(userId: String): List<TourEntity> {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("tours")
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val firebaseTours = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirebaseTour::class.java)?.toEntity()
                }
                dao.deleteAllTours()
                firebaseTours.forEach { tour ->
                    dao.insertTour(tour)
                }

                firebaseTours
            } else {
                dao.getToursByUser(userId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dao.getToursByUser(userId)
        }
    }
    override suspend fun getTourById(id: Long): TourEntity? {
        val localTour = dao.getTourById(id)
        if (localTour != null) {
            return localTour
        }

        auth.currentUser?.uid?.let { uid ->
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("tours")
                .whereEqualTo("id", id)
                .get()
                .await()

            val firebaseTour = snapshot.documents.firstOrNull()?.toObject(FirebaseTour::class.java)?.toEntity()
            if (firebaseTour != null) {
                dao.insertTour(firebaseTour)
            }
            return firebaseTour
        }

        // Return null if the tour is not found
        return null
    }

    override suspend fun updateTour(tour: TourEntity) {
        dao.updateTour(tour)

        auth.currentUser?.uid?.let { uid ->
            val firebaseTour = tour.toFirebase()
            firestore.collection("users")
                .document(uid)
                .collection("tours")
                .document(firebaseTour.id)
                .set(firebaseTour)
                .await()
        }
    }

    override suspend fun deleteAllTours() {
        dao.deleteAllTours()

        auth.currentUser?.uid?.let { uid ->
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("tours")
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
        }
    }

    override suspend fun deleteTourById(id: Long) {
        val tour = dao.getTourById(id)
        tour?.let {
            dao.deleteTourById(id)

            auth.currentUser?.uid?.let { uid ->
                firestore.collection("users")
                    .document(uid)
                    .collection("tours")
                    .whereEqualTo("startTime", tour.startTime)
                    .get()
                    .await()
                    .forEach { it.reference.delete().await() }
            }
        }
    }
}
