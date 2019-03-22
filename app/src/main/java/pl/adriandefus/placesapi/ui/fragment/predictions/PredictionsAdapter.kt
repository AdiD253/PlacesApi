package pl.adriandefus.placesapi.ui.fragment.predictions

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_place.view.*
import pl.adriandefus.placesapi.R
import pl.adriandefus.placesapi.domain.model.PlaceInformation

class PredictionsAdapter(
    private val predictionsClickCallback: PredictionsClickCallback
) : ListAdapter<PlaceInformation, PredictionViewHolder>(PlaceInformation.DIFF_CALLBACK) {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        if (!this::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater.inflate(R.layout.item_place, parent, false)
        return PredictionViewHolder(view, predictionsClickCallback)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PredictionViewHolder(
    view: View,
    private val predictionsClickCallback: PredictionsClickCallback
) : RecyclerView.ViewHolder(view) {

    fun bind(placeInformation: PlaceInformation) {
        itemView.placeDescription.text = placeInformation.description

        val favouriteIconDrawable = getFavouriteIconDrawable(placeInformation.isFavourite)
        itemView.favouriteIcon.setImageDrawable(favouriteIconDrawable)

        itemView.favouriteIcon.setOnClickListener {
            predictionsClickCallback.onMarkAsFavouriteSelected(placeInformation)
        }

        itemView.setOnClickListener {
            predictionsClickCallback.onPredictionSelected(placeInformation.placeId)
        }
    }

    private fun getFavouriteIconDrawable(isPlaceFavourite: Boolean): Drawable? {
            return if (isPlaceFavourite)
                ContextCompat.getDrawable(itemView.context, R.drawable.heart_full)?.apply {
                    colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY
                    )
                }
            else
                ContextCompat.getDrawable(itemView.context, R.drawable.heart_empty)
    }
}

interface PredictionsClickCallback {
    fun onPredictionSelected(placeId: String)
    fun onMarkAsFavouriteSelected(placeInformation: PlaceInformation)
}