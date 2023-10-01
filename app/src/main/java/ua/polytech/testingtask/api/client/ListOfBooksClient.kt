package ua.polytech.testingtask.api.client

import retrofit2.http.*
import ua.polytech.testingtask.api.models.ResponseListOfBooks
import ua.polytech.testingtask.api.models.ResponseListOfBooksOfAllCategories

// client api
interface ListOfBooksClient {
    @GET("/svc/books/v3/lists/current/{list_name_encoded}.json")
    suspend fun getListOfBooks(@Path("list_name_encoded") listNameEncoded: String): ResponseListOfBooks

    @GET("/svc/books/v3/lists/full-overview.json")
    suspend fun getAllListOfBooks(): ResponseListOfBooksOfAllCategories

}