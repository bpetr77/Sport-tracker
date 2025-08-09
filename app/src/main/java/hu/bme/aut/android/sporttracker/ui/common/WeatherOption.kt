package hu.bme.aut.android.sporttracker.ui.common

import androidx.annotation.StringRes
import hu.bme.aut.android.sporttracker.R

enum class WeatherOption(@StringRes val labelResId: Int) {
    SUNNY(R.string.weather_sunny),
    CLOUDY(R.string.weather_cloudy),
    RAINY(R.string.weather_rainy),
    SNOWY(R.string.weather_snowy),
    WINDY(R.string.weather_windy)
}