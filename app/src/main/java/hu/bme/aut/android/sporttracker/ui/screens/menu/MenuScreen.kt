package hu.bme.aut.android.sporttracker.ui.screens.menu

import android.app.Activity
import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.sporttracker.ui.screens.main.MainLayout
import java.util.Locale

@Composable
fun MenuScreen(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onToursClick: () -> Unit,
    onMapClick: () -> Unit,
    onAllToursClick: () -> Unit
) {
    MainLayout(
        iconTint = Color.White,
        drawerState = drawerState,
        onMenuClick = onMenuClick,
        onToursClick = onToursClick,
        onMapClick = onMapClick,
        onAllToursClick = onAllToursClick
    ) {
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var selectedLanguage by remember { mutableStateOf("Magyar") }

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
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nyelv") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.Blue,
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Blue,
                        disabledTrailingIconColor = Color.Blue
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
                                selectedLanguage = languageName
                                expanded = false
                                switchToLanguage(context, locale)
                            }
                        )
                    }
                }
            }
        }
    }
}

fun switchToLanguage(context: Context, locale: Locale) {
    val resources = context.resources
    val config = Configuration(resources.configuration)

    Locale.setDefault(locale)
    config.setLocale(locale)

    // Context-frissítés
    resources.updateConfiguration(config, resources.displayMetrics)

    // Az Activity újraindítása, hogy a nyelv érvénybe lépjen
    (context as? Activity)?.recreate()
}