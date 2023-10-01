package ua.polytech.testingtask.db

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow
import ua.polytech.testingtask.api.models.Catalog
import ua.polytech.testingtask.api.models.ListOfBooksModel
import ua.polytech.testingtask.api.models.ResultsListOfBooksOfCategory

@Dao
interface RoomDao {

    @Query("SELECT * FROM catalogs_table")
    fun getCatalogs(): Flow<List<Catalog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalog(catalog: Catalog?)

    @Query("SELECT * FROM books_table ")
    fun getListOfBooksAllCategories(): Flow<List<ResultsListOfBooksOfCategory>>

    @Query("SELECT * FROM books_table WHERE listNameEncoded = :listNameEncoded")
    suspend fun getBooksByListName(listNameEncoded: String): ListOfBooksModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: ResultsListOfBooksOfCategory)


}