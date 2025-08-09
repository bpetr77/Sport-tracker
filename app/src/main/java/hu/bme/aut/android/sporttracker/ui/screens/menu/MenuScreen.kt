package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.R
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout
import hu.bme.aut.android.sporttracker.ui.sign_in.UserData
import hu.bme.aut.android.sporttracker.ui.viewModels.SettingsViewModel
import java.util.Locale

@Composable
fun MenuScreen(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onAllToursClick: () -> Unit,
    viewModel: SettingsViewModel,
    userData: UserData?,
    onSignOut: () -> Unit
){
    MainLayout(
        iconTint = Color.White,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick,
        userData = userData,
        onSignOut = onSignOut
    ) {
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        val selectedLanguage by viewModel.selectedLanguage.collectAsState()
        val activity = getActivity()

        val languages = listOf(
            "Magyar" to Locale("hu"),
            "English" to Locale("en"),
            "Deutsch" to Locale("de"),
            "Español" to Locale("es")
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(80.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
            ) {
                OutlinedTextField(
                    value = selectedLanguage.displayLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.language))},
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = Color(0xFFFFFFFF),
                        disabledLabelColor = Color(0xFF468FF3),
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color(0xFF468FF3),
                        disabledTrailingIconColor = Color(0xFFFFFFFF)
                    )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    languages.forEach { (languageName, locale) ->
                        DropdownMenuItem(
                            text = { Text(languageName) },
                            onClick = {
                                viewModel.updateSelectedLanguage(locale)
                                expanded = false
                                activity?.let { switchToLanguage(it, locale) }
                            }
                        )
                    }
                }
            }
        }
    }
}

// TODO: get this out of here
fun switchToLanguage(activity: Activity, locale: Locale) {
    val resources = activity.resources
    val config = Configuration(resources.configuration)

    Locale.setDefault(locale)
    config.setLocale(locale)

    activity.baseContext.resources.updateConfiguration(config, resources.displayMetrics)

    restartActivity(activity)
}

@Composable
fun getActivity(): Activity? {
    val context = LocalContext.current
    return when (context) {
        is Activity -> context
        is ContextWrapper -> {
            var wrapper = context
            while (wrapper is ContextWrapper) {
                if (wrapper is Activity) return wrapper
                wrapper = wrapper.baseContext as? ContextWrapper ?: break
            }
            null
        }
        else -> null
    }
}

fun restartActivity(activity: Activity) {
    val intent = Intent(activity, activity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    activity.finish()
    activity.overridePendingTransition(0, 0)
    activity.startActivity(intent)
    activity.overridePendingTransition(0, 0)
}