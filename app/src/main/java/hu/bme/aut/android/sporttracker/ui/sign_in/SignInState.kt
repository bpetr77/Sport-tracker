package hu.bme.aut.android.sporttracker.ui.sign_in

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)
