package hu.bme.aut.android.sporttracker.data.routePlanner.model

data class RouteSegment(
    val t: String, // type: secondary, tertiary, stb.
    val c: List<List<Double>>, // list of [lat, lon]
    val o: String? = null, // oneway
    val b: String? = null, // cycle lane
) {
    fun toLatLngList(): List<Pair<Double, Double>> {
        return c.map { Pair(it[0], it[1]) }
    }
}