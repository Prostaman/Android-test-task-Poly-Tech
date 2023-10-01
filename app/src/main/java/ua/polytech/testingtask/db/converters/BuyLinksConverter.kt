package ua.polytech.testingtask.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.polytech.testingtask.api.models.BuyLink

class BuyLinksConverter {
    @TypeConverter
    fun fromBuyLinksList(buyLinks: List<BuyLink>): String {
        val gson = Gson()
        return gson.toJson(buyLinks)
    }

    @TypeConverter
    fun toBuyLinksList(buyLinksString: String): List<BuyLink> {
        val gson = Gson()
        val type = object : TypeToken<List<BuyLink>>() {}.type
        return gson.fromJson(buyLinksString, type)
    }
}