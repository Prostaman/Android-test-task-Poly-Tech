package ua.polytech.testingtask.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.polytech.testingtask.api.models.Catalog
import ua.polytech.testingtask.api.models.ResultsListOfBooksOfCategory
import ua.polytech.testingtask.db.converters.BookListConverter
import ua.polytech.testingtask.db.converters.BuyLinksConverter

@Database(entities = [Catalog::class, ResultsListOfBooksOfCategory::class], version = 1, exportSchema = false)
@TypeConverters(BuyLinksConverter::class, BookListConverter::class)
abstract class LibraryRoomDatabase : RoomDatabase() {

    abstract fun libraryDao(): RoomDao

    companion object {
        const val LIBRARY_DB =  "library_database"
    }

}

