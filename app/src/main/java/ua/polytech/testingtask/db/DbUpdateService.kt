package ua.polytech.testingtask.db

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.polytech.testingtask.api.client.ListOfBooksClient
import ua.polytech.testingtask.api.models.Book
import ua.polytech.testingtask.api.models.ListOfBooksModel
import ua.polytech.testingtask.api.models.ResultsListOfBooksOfCategory
import javax.inject.Inject
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult


@AndroidEntryPoint
class DbUpdateService : Service() {
    @Inject
    lateinit var booksClient: ListOfBooksClient
    @Inject
    lateinit var repository: RoomRepository
    @Inject
    lateinit var imageLoader: ImageLoader

    private val networkRequestsScope = CoroutineScope(Dispatchers.IO)
    private val dbScope = CoroutineScope(Dispatchers.Main)


    override fun onBind(intent: Intent?): IBinder? {
        // Implement onBind if necessary
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Perform the download and database update tasks here
        getAllListOfBooks()
        return START_STICKY
    }


    private fun getAllListOfBooks() {
        networkRequestsScope.launch {
            try {
                val response = booksClient.getAllListOfBooks()
               // initImageLoader()
                updateDB(response.resultsListOfBooksOfCategory.lists)
                Log.d(logOfService, response.resultsListOfBooksOfCategory.lists.toString())
            } catch (e: Exception) {
                Log.d(logOfService, e.toString())
            }
        }
    }


    private fun updateDB(networkDataBooks: List<ListOfBooksModel>) {
        fun isDifferenceInBooks(
            networkData: List<Book>,
            localData: List<Book>
        ): Boolean {
            for (networkBook in networkData) {
                localData.find {
                    it == networkBook
                } ?: return true
            }
            return false
        }

        dbScope.launch {
            repository.getListOfBooksAllCategories().collect { localData ->
                networkDataBooks.forEach { catalog ->
                    val index =
                        localData.indexOf(localData.find { it.listNameEncoded == catalog.listNameEncoded })
                    if (index != -1) {
                        if (isDifferenceInBooks(catalog.books, localData[index].books)) {
                            repository.insertBooks(
                                ResultsListOfBooksOfCategory(
                                    listNameEncoded = catalog.listNameEncoded,
                                    books = catalog.books
                                )
                            )
                        }
                    } else {
                        repository.insertBooks(
                            ResultsListOfBooksOfCategory(
                                listNameEncoded = catalog.listNameEncoded,
                                books = catalog.books
                            )
                        )
                    }
                }
                localData.forEach { listOfBooks ->
                    downloadAndCacheImages(
                        ListOfBooksModel(
                            listNameEncoded = listOfBooks.listNameEncoded,
                            books = listOfBooks.books
                        )
                    )
                }
            }

        }
    }

    private fun downloadAndCacheImages(catalog: ListOfBooksModel) {
        catalog.books.forEach { book ->
            if ((book.bookImage ?: "").isNotEmpty()) {
                downloadAndCacheImage(book.bookImage!!)
            }
        }
    }

    private fun downloadAndCacheImage(imageUrl: String) {
        networkRequestsScope.launch {
            val request = ImageRequest.Builder(applicationContext)
                .data(imageUrl)
                .build()
            // Загрузка изображения и кеширование в фоновом потоке
            try {
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    Log.d(logOfService, "Success: $imageUrl")
                    // Изображение успешно загружено и закэшировано
                } else {
                    Log.d(logOfService, "Error: $imageUrl")
                    // Обработайте ошибку, если не удалось загрузить или закэшировать изображение
                }
            } catch (e: Exception) {
                Log.d(logOfService, "Error: $imageUrl, Exception:$e")
            }
        }

    }

    companion object {
        const val logOfService = "DownloadService"
    }


}