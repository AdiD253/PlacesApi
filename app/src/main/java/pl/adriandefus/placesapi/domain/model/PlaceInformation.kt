package pl.adriandefus.placesapi.domain.model

import androidx.recyclerview.widget.DiffUtil
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PlaceInformation(
    @PrimaryKey
    var id: String = "",

    var placeId: String = "",
    var description: String = "",
    var isFavourite: Boolean = false
) : RealmObject() {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlaceInformation>() {
            override fun areItemsTheSame(oldItem: PlaceInformation, newItem: PlaceInformation) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlaceInformation, newItem: PlaceInformation) =
                oldItem.getDiff() == newItem.getDiff()
        }
    }

    fun getDiff(): String =
        placeId + description + isFavourite
}