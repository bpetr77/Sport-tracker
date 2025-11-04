package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.sporttracker.data.repository.impl.LocationRepositoryImpl
import hu.bme.aut.android.sporttracker.domain.repository.TourRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase

class TourStartedSettingsViewModelFactory(
    private val locationRepositoryImpl: LocationRepositoryImpl,
    private val tourUseCase: TourUseCase,
    private val tourRepository: TourRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourStartedSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourStartedSettingsViewModel(
                locationRepositoryImpl, tourUseCase,
                tourRepository = tourRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}