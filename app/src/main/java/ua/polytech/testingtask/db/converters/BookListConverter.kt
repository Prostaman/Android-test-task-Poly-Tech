package ua.polytech.testingtask.db.converters
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.polytech.testingtask.api.models.Book
class BookListConverter {
    @TypeConverter
    fun fromString(value: String): List<Book> {
        val listType = object : TypeToken<List<Book>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(value: List<Book>): String {
        val gson = Gson()
        return gson.toJson(value)
    }
}