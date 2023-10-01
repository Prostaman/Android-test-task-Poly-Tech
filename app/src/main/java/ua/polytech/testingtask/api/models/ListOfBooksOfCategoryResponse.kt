package ua.polytech.testingtask.api.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Keep
data class ResponseListOfBooks(
    //@SerializedName("status") var status       : String?  = null,
    //@SerializedName("copyright") var copyright    : String?  = null,
    // @SerializedName("num_results") var numResults   : Int?     = null,
    // @SerializedName("last_modified") var lastModified : String?  = null,
    @SerializedName("results") val resultsListOfBooksOfCategory: ResultsListOfBooksOfCategory

)

@Keep
@Entity(tableName = "books_table")
data class ResultsListOfBooksOfCategory(
    // @SerializedName("list_name") var listName: String,
    @PrimaryKey(autoGenerate = false) @SerializedName("list_name_encoded") var listNameEncoded: String,
    //@SerializedName("bestsellers_date") var bestsellersDate: String? = null,
    //@SerializedName("published_date") var publishedDate: String? = null,
    //@SerializedName("published_date_description") var publishedDateDescription: String? = null,
    //@SerializedName("next_published_date") var nextPublishedDate: String? = null,
    //@SerializedName("previous_published_date") var previousPublishedDate: String? = null,
    //@SerializedName("display_name") var displayName: String? = null,
    //@SerializedName("normal_list_ends_at") var normalListEndsAt: Int? = null,
    //@SerializedName("updated") var updated: String? = null,
    @ColumnInfo @SerializedName("books") var books: List<Book> = emptyList(),
    //@SerializedName("corrections") var corrections: ArrayList<String> = arrayListOf()
)

//ResponseModel
@Keep
data class Book(
    @SerializedName("title") var title: String = "",
    @SerializedName("author") var author: String = "",
    @SerializedName("rank") var rank: Int = 0,
    //@SerializedName("rank_last_week") var rankLastWeek: Int? = null,
    //@SerializedName("weeks_on_list") var weeksOnList: Int? = null,
    //@SerializedName("asterisk") var asterisk: Int? = null,
    //@SerializedName("dagger") var dagger: Int? = null,
    //@SerializedName("primary_isbn10") var primaryIsbn10: String? = null,
    //@SerializedName("primary_isbn13") var primaryIsbn13: String? = null,
    @SerializedName("publisher") var publisher: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("price") var price: String? = null,
    //@SerializedName("contributor") var contributor: String? = null,
    //@SerializedName("contributor_note") var contributorNote: String? = null,
    @SerializedName("book_image") var bookImage: String? = null,
    // @SerializedName("book_image_width") var bookImageWidth: Int? = null,
    //@SerializedName("book_image_height") var bookImageHeight: Int? = null,
    // @SerializedName("amazon_product_url") var amazonProductUrl: String? = null,
    // @SerializedName("age_group") var ageGroup: String? = null,
    //@SerializedName("book_review_link") var bookReviewLink: String? = null,
    // @SerializedName("first_chapter_link") var firstChapterLink: String? = null,
    // @SerializedName("sunday_review_link") var sundayReviewLink: String? = null,
    //@SerializedName("article_chapter_link") var articleChapterLink: String? = null,
    //@SerializedName("isbns") var isbns: ArrayList<Isbn> = arrayListOf(),
    @SerializedName("buy_links") var buyLinks: List<BuyLink> = emptyList(),
    // @SerializedName("book_uri") var bookUri: String? = null

)

//@Keep
//data class Isbn(
//    @SerializedName("isbn10") var isbn10: String? = null,
//    @SerializedName("isbn13") var isbn13: String? = null
//)

@Keep
data class BuyLink(
    //@SerializedName("name") var name: String? = null,
    @SerializedName("url") var url: String = ""
)




