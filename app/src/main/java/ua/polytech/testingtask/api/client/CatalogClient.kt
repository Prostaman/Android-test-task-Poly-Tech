package ua.polytech.testingtask.api.client

import retrofit2.http.*
import ua.polytech.testingtask.api.models.ResponseCatalog

// client api
interface CatalogClient {
    @GET("/svc/books/v3/lists/names.json")
    suspend fun getCatalog(): ResponseCatalog
}