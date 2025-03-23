package hu.bme.aut.android.sporttracker.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Screen1: Screen("screen1")
    object Screen2: Screen("screen2")
}