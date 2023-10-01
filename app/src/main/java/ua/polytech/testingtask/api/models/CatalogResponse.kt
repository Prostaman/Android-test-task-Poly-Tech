package ua.polytech.testingtask.api.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Keep
data class ResponseCatalog(
    val results: List<Catalog>
)

//ResponseModel
@Keep
@Entity(tableName = "catalogs_table")
data class Catalog(
    @PrimaryKey(autoGenerate = false) @SerializedName("list_name") val listName: String,
    @ColumnInfo @SerializedName("display_name") val displayName: String,
    @ColumnInfo @SerializedName("list_name_encoded") val listNameEncoded: String,
   // @SerializedName("oldest_published_date") val oldestPublishedDate: String="",
    @ColumnInfo @SerializedName("newest_published_date") val newestPublishedDate: String,
    @ColumnInfo @SerializedName("updated")val updated: String
)


