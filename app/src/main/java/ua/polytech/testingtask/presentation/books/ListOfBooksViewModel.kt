package ua.polytech.testingtask.presentation.books

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
import ua.polytech.testingtask.api.models.ListOfBooksModel
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
                updateListOfBooksInDB(
                    ListOfBooksModel(
                        listNameEncoded = response.resultsListOfBooksOfCategory.listNameEncoded,
                        books = response.resultsListOfBooksOfCategory.books
                    )
                )
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
                val response = repository.getBooksByListName(listNameEncoded)
                _requestListOfBooks.value = Resource.success(
                    ResultsListOfBooksOfCategory(
                        listNameEncoded = response.listNameEncoded,
                        books = response.books
                    )
                )
                _suggestedBooks.value = response.books
            } catch (e: Exception) {
                Log.d("RequestBooksLocal", e.stackTraceToString())
                if (e is NullPointerException){
                    _requestListOfBooks.value = Resource.error("Empty list of books, please try  download it with Internet", null)
                }else{
                    _requestListOfBooks.value = Resource.error("Error: $e", null)
                }

            }
        }
    }

    private fun updateListOfBooksInDB(listOfBooksModel: ListOfBooksModel) {
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

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val localListOfBooks = repository.getBooksByListName(listOfBooksModel.listNameEncoded)
                if (isDifferenceInBooks(networkData = listOfBooksModel.books, localData = localListOfBooks.books)) {
                    repository.insertBooks(ResultsListOfBooksOfCategory(listNameEncoded = listOfBooksModel.listNameEncoded, books = listOfBooksModel.books))
                }
            } catch (e: NullPointerException) {
                repository.insertBooks(ResultsListOfBooksOfCategory(listNameEncoded = listOfBooksModel.listNameEncoded, books = listOfBooksModel.books))
            } catch (e: Exception) {
                Log.d("UpdatingLocalList", e.toString())
            }
        }
    }


}

