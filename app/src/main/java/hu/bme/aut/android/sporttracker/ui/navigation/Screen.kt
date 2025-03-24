package hu.bme.aut.android.sporttracker.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Menu: Screen("menu")
    object Screen2: Screen("screen2")
}