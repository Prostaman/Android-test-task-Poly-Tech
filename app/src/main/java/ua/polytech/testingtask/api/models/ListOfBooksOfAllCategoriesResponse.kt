package ua.polytech.testingtask.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseListOfBooksOfAllCategories(
    // @SerializedName("status"      ) var status     : String?  = null,
    // @SerializedName("copyright"   ) var copyright  : String?  = null,
    // @SerializedName("num_results" ) var numResults : Int?     = null,
    @SerializedName("results") var resultsListOfBooksOfCategory: ResultsListOfBooksOfAllCategories
)

@Keep
data class ResultsListOfBooksOfAllCategories(
//    @SerializedName("bestsellers_date") var bestsellersDate: String? = null,
//    @SerializedName("published_date") var publishedDate: String? = null,
//    @SerializedName("published_date_description") var publishedDateDescription: String? = null,
//    @SerializedName("previous_published_date") var previousPublishedDate: String? = null,
//    @SerializedName("next_published_date") var nextPublishedDate: String? = null,
    @SerializedName("lists") var lists: ArrayList<ListOfBooksModel> = arrayListOf()
)
@Keep
data class ListOfBooksModel(
//    @SerializedName("list_id") var listId: Int? = null,
//    @SerializedName("list_name") var listName: String = "null",
    @SerializedName("list_name_encoded") var listNameEncoded: String ="null",
//    @SerializedName("display_name") var displayName: String? = null,
//    @SerializedName("updated") var updated: String? = null,
//    @SerializedName("list_image") var listImage: String? = null,
//    @SerializedName("list_image_width") var listImageWidth: String? = null,
//    @SerializedName("list_image_height") var listImageHeight: String? = null,
    @SerializedName("books") var books: List<Book> = listOf()
)

