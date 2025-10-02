package hu.bme.aut.android.sporttracker.data.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import hu.bme.aut.android.sporttracker.data.model.LocationPoint

class Converters {
    @TypeConverter
    fun fromLocationPointList(value: List<LocationPoint>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLocationPointList(value: String): List<LocationPoint> {
        val type = object : TypeToken<List<LocationPoint>>() {}.type
        return Gson().fromJson(value, type)
    }
}