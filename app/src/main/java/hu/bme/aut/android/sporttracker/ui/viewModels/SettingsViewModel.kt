package hu.bme.aut.android.sporttracker.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SettingsViewModel : ViewModel() {
    var isDarkModeEnabled = false

    private val _selectedLanguage = MutableStateFlow(Locale("hu"))
    val selectedLanguage: StateFlow<Locale> = _selectedLanguage

    fun toggleDarkMode() {
        isDarkModeEnabled = !isDarkModeEnabled
    }

    // Update function for selectedLanguage
    fun updateSelectedLanguage(locale: Locale) {
        _selectedLanguage.value = locale
    }
}