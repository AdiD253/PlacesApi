package pl.adriandefus.placesapi.ui.fragment.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_favourite.view.*
import pl.adriandefus.placesapi.R
import pl.adriandefus.placesapi.domain.model.PlaceInformation

class FavouritesAdapter(
    private val results: RealmResults<PlaceInformation>?,
    private val predictionsClickCallback: FavouriteClickCallback
) : RealmRecyclerViewAdapter<PlaceInformation, FavouriteViewHolder>(results, true) {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        if (!this::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater.inflate(R.layout.item_favourite, parent, false)
        return FavouriteViewHolder(view, predictionsClickCallback)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(results?.getOrNull(position))
    }
}

class FavouriteViewHolder(
    view: View,
    private val predictionsClickCallback: FavouriteClickCallback
) : RecyclerView.ViewHolder(view) {

    fun bind(placeInformation: PlaceInformation?) {
        placeInformation ?: return
        itemView.favouriteDescription.text = placeInformation.description

        itemView.favouriteRemove.setOnClickListener {
            predictionsClickCallback.onRemoveItemSelected(placeInformation)
        }

        itemView.setOnClickListener {
            predictionsClickCallback.onFavouriteSelected(placeInformation.placeId)
        }
    }
}

interface FavouriteClickCallback {
    fun onFavouriteSelected(placeId: String)
    fun onRemoveItemSelected(placeInformation: PlaceInformation)
}