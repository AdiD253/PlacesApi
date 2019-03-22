package pl.adriandefus.placesapi.remote.model

import com.google.gson.annotations.SerializedName

class PlaceAutocompleteDto(
    @SerializedName("predictions")
    val predictions: List<PredictionDto>
) : PlaceBaseResponse()

class PredictionDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("description")
    val description: String
)