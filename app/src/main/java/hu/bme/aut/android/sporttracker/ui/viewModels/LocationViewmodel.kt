package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewmodel : ViewModel() {
    var locationPermissionGranted = mutableStateOf(false)
        private set

    fun updatePermissionGranted(isGranted: Boolean) {
        locationPermissionGranted.value = isGranted
    }
}