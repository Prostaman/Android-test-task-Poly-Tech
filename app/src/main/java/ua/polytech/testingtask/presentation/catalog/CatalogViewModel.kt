package ua.polytech.testingtask.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.polytech.testingtask.api.client.CatalogClient
import ua.polytech.testingtask.api.other.Resource
import javax.inject.Inject
import kotlinx.coroutines.launch
import ua.polytech.testingtask.api.models.Catalog
import ua.polytech.testingtask.db.RoomRepository

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val catalogClient: CatalogClient,
    private val repository: RoomRepository
) : ViewModel() {

    private val _requestCatalog = MutableStateFlow<Resource<List<Catalog>>>(Resource.loading(null))
    val requestCatalog: StateFlow<Resource<List<Catalog>?>> = _requestCatalog.asStateFlow()
    fun getCatalogFromNetwork() {
        _requestCatalog.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = catalogClient.getCatalog()
                _requestCatalog.value = Resource.success(response.results)
                _suggestedCategories.value = response.results
                compareAndUpdateLocalCatalog()
            } catch (e: Exception) {
                getCatalogFromLocalBD()
            }
        }
    }

    private val _suggestedCategories = MutableStateFlow<List<Catalog>>(emptyList())
    val suggestedCategories: StateFlow<List<Catalog>> = _suggestedCategories.asStateFlow()

    fun textOfSearchChanged(text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val newCategories = _requestCatalog.value.data?.filter {
                it.displayName.lowercase().contains(text.lowercase())
            }

            _suggestedCategories.value = newCategories ?: emptyList()
        }
    }

    fun onTextEmpty() {
        _suggestedCategories.value = _requestCatalog.value.data ?: emptyList()
    }

    // Function to compare and update data in the local database
    private fun compareAndUpdateLocalCatalog() {
        fun findDifferencesCatalogs(
            networkData: List<Catalog>,
            localData: List<Catalog>
        ): List<Catalog> {
            val newDataToInsert = mutableListOf<Catalog>()
            for (networkItem in networkData) {
                val matchingLocalItem = localData.find {
                    it == networkItem
                }
                if (matchingLocalItem == null) {
                    // If there is no matching item in the local database, add it to newDataToInsert
                    //removeOldCatalogFromLocalDB(localData = localData, networkItem = networkItem)
                    newDataToInsert.add(networkItem)
                }
            }
            return newDataToInsert
        }

        viewModelScope.launch(Dispatchers.Main) {
            val networkDataCatalogs = _requestCatalog.value.data
            repository.getCatalogs().collect { localData ->
                val newDataToInsert = findDifferencesCatalogs(networkDataCatalogs ?: emptyList(), localData)
                newDataToInsert.forEach { catalog ->
                    repository.insertCatalog(catalog)
                }
            }
        }
    }

    fun getCatalogFromLocalBD() {
        _requestCatalog.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                repository.getCatalogs().collect { localData ->
                    if (localData.isNotEmpty()) {
                        if (localData.size > (_requestCatalog.value.data?.size ?: 0)) {
                            _requestCatalog.value = Resource.success(localData)
                            _suggestedCategories.value = localData
                        }
                    } else {
                        _requestCatalog.value = Resource.error("Error: Empty local storage", null)
                    }
                }
            } catch (e: Exception) {
                _requestCatalog.value = Resource.error("Error: $e", null)
            }
        }
    }


}

