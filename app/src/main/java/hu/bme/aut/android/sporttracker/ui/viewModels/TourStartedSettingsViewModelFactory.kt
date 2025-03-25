package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
import hu.bme.aut.android.sporttracker.domain.usecase.TourUseCase

//class TourStartedSettingsViewModelFactory(
//    private val locationRepository: LocationRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TourStartedSettingsViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return TourStartedSettingsViewModel(locationRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}


class TourStartedSettingsViewModelFactory(
    private val locationRepository: LocationRepository,
    private val tourUseCase: TourUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourStartedSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourStartedSettingsViewModel(locationRepository, tourUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}