package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.sporttracker.data.repository.impl.TourRepositoryImpl
import hu.bme.aut.android.sporttracker.data.repository.location.LocationRepository
import hu.bme.aut.android.sporttracker.data.repository.location.TourRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase

class TourStartedSettingsViewModelFactory(
    private val locationRepository: LocationRepository,
    private val tourUseCase: TourUseCase,
    private val tourRepository: TourRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourStartedSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourStartedSettingsViewModel(
                locationRepository, tourUseCase,
                tourRepository = tourRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}