package ua.polytech.testingtask.books

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.polytech.testingtask.api.other.Resource
import javax.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ua.polytech.testingtask.api.client.ListOfBooksClient
import ua.polytech.testingtask.api.models.Book
import ua.polytech.testingtask.api.models.ResultsListOfBooksOfCategory
import ua.polytech.testingtask.db.RoomRepository

@HiltViewModel
class ListOfBooksViewModel @Inject constructor(private val client: ListOfBooksClient, private val repository: RoomRepository) : ViewModel() {

    private val _requestListOfBooks = MutableStateFlow<Resource<ResultsListOfBooksOfCategory>>(Resource.loading(null))
    val requestListOfBooks: StateFlow<Resource<ResultsListOfBooksOfCategory?>> = _requestListOfBooks.asStateFlow()

    fun getListOfBooksFromNetworkDB(listNameEncoded: String) {
        _requestListOfBooks.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.getListOfBooks(listNameEncoded = listNameEncoded)
                Log.d("RequestListOfBooks", response.toString())
                _requestListOfBooks.value = Resource.success(response.resultsListOfBooksOfCategory)
                _suggestedBooks.value = response.resultsListOfBooksOfCategory.books
            } catch (e: HttpException) {
                Log.d("RequestListOfBooks", e.stackTraceToString())
                getListOfBooksFromLocalDB(listNameEncoded = listNameEncoded)
            }
        }
    }

    private val _suggestedBooks = MutableStateFlow<List<Book>>(emptyList())
    val suggestedBooks: StateFlow<List<Book>> = _suggestedBooks.asStateFlow()

    fun textOfSearchChanged(text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val newBooks = _requestListOfBooks.value.data?.books?.filter {
                it.title.lowercase().contains(text.lowercase()) || it.author.lowercase().contains(text.lowercase())
            }
            _suggestedBooks.value = newBooks ?: emptyList()
        }
    }

    fun textOfSearchEmpty() {
        _suggestedBooks.value = _requestListOfBooks.value.data?.books ?: emptyList()
    }

    fun getListOfBooksFromLocalDB(listNameEncoded: String) {
        _requestListOfBooks.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.Main) {
            _requestListOfBooks.value = Resource.loading(null)
            try {
                Log.d("TestingListNameEncoded","listNameEncoded:${listNameEncoded}")
                val response = repository.getBooksByListName(listNameEncoded)
                Log.d("TestingListNameEncoded","reponse:${response}")
                _requestListOfBooks.value = Resource.success(
                    ResultsListOfBooksOfCategory(
                        listNameEncoded = response.listNameEncoded,
                        books = response.books
                    )
                )
                _suggestedBooks.value = response.books
            } catch (e: Exception) {
                Log.d("RequestBooksLocal", e.stackTraceToString())
                _requestListOfBooks.value = Resource.error("Error: $e", null)
            }
        }

    }

}

