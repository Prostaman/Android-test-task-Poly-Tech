package ua.polytech.testingtask.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ua.polytech.testingtask.api.models.Catalog
import ua.polytech.testingtask.api.models.ListOfBooksModel
import ua.polytech.testingtask.api.models.ResultsListOfBooksOfCategory
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RoomRepository @Inject constructor(private val roomDao: RoomDao) :
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun getCatalogs() = roomDao.getCatalogs()

    suspend fun insertCatalog(catalog: Catalog) {
        roomDao.insertCatalog(catalog)
    }


    fun getListOfBooksAllCategories() = roomDao.getListOfBooksAllCategories()

    suspend fun getBooksByListName(listNameEncoded: String): ListOfBooksModel {
        return roomDao.getBooksByListName(listNameEncoded)
    }

    suspend fun insertBooks(books: ResultsListOfBooksOfCategory) {
        roomDao.insertBooks(books)
    }


}