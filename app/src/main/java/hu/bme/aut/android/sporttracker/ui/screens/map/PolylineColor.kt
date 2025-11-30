package hu.bme.aut.android.sporttracker.ui.screens.map

import androidx.compose.ui.graphics.Color

fun getPolylineColor(type: String): Color {
    return when (type) {
        // --- ZÖLD: Bringabarát utak (kis súly) ---
        "cycleway" -> Color(0xFF5BA45C)
        "track", "path", "footway" -> Color(0xFF2337AD)

        // --- SÁRGA/NARANCS: Lakóövezet és kisebb utak (közepes súly) ---
        "residential", "living_street", "unclassified" -> Color(0xFFA7B429) // Lime
        "tertiary", "tertiary_link", "service", "pedestrian" -> Color(0xFFD3BA00) // Sárga

        // --- NARANCS/PIROS: Forgalmasabb utak (nagyobb súly) ---
        "secondary", "secondary_link", "busway", "bridleway" -> Color(0xFFFF9800) // Narancs

        // --- PIROS/FEKETE: Veszélyes/Tiltott utak (hatalmas súly) ---
        "primary", "primary_link"  -> Color(0xFFF44336) // Piros
        "trunk", "trunk_link" -> Color(0xFFB71C1C) // Sötétvörös
        "motorway", "motorway_link", "road" -> Color.Black // Fekete (autópálya)

        "steps" -> Color(0xFF81D4FA)
        // --- EGYÉB ---
        else -> Color.Gray // Ismeretlen
    }
}