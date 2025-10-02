package hu.bme.aut.android.sporttracker.data.repository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.android.sporttracker.data.local.database.TourDao
import hu.bme.aut.android.sporttracker.data.local.model.TourEntity
import hu.bme.aut.android.sporttracker.data.mappers.toEntity
import hu.bme.aut.android.sporttracker.data.mappers.toFirebase
import hu.bme.aut.android.sporttracker.data.remote.firebase.model.FirebaseTour
import kotlinx.coroutines.tasks.await

class TourRepositoryImpl(
    private val dao: TourDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun addTour(tour: TourEntity) {
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
    suspend fun getUserTours2(userId: String): List<TourEntity> {
        val localTours = dao.getToursByUser(userId)
        if(localTours.isNotEmpty()) return localTours
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("tours")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseTour::class.java)?.toEntity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun getUserTours(userId: String): List<TourEntity> {
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

                // 🔹 cache-eljük DB-be, hogy offline is elérhető legyen
                firebaseTours.forEach { tour ->
                    dao.insertTour(tour)
                }

                firebaseTours
            } else {
                // 🔹 ha nincs Firebase adat → helyi DB
                dao.getToursByUser(userId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 🔹 ha hiba van (pl. nincs net) → helyi DB
            dao.getToursByUser(userId)
        }
    }
    suspend fun getTourById(id: Long): TourEntity? {
        // Attempt to fetch the tour from the local database
        val localTour = dao.getTourById(id)
        if (localTour != null) {
            return localTour
        }

        // If not found locally, fetch from Firebase
        auth.currentUser?.uid?.let { uid ->
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("tours")
                .whereEqualTo("id", id)
                .get()
                .await()

            val firebaseTour = snapshot.documents.firstOrNull()?.toObject(FirebaseTour::class.java)?.toEntity()
            if (firebaseTour != null) {
                // Cache the fetched tour locally
                dao.insertTour(firebaseTour)
            }
            return firebaseTour
        }

        // Return null if the tour is not found
        return null
    }

    suspend fun getAllTours(): List<TourEntity> {
        // Először lokális adatok
        val localTours = dao.getAllTours()
        if (localTours.isNotEmpty()) return localTours

        // Ha üres → Firebase-ből töltsük le és cache-eljük
        auth.currentUser?.uid?.let { uid ->
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("tours")
                .get()
                .await()

            val tours = snapshot.documents.mapNotNull {
                it.toObject(FirebaseTour::class.java)?.toEntity()
            }

            tours.forEach { dao.insertTour(it) }
            return tours
        }

        return emptyList()
    }

    suspend fun updateTour(tour: TourEntity) {
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

    suspend fun deleteAllTours() {
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

    suspend fun deleteTourById(id: Long) {
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
