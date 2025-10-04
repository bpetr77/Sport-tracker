package hu.bme.aut.android.sporttracker.data.remote.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun provideFirestore(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}

fun provideFirebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}