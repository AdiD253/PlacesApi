package pl.adriandefus.placesapi.remote.model

import com.google.gson.annotations.SerializedName

open class PlaceBaseResponse(
    @SerializedName("status")
    var status: String? = null,

    @SerializedName("error_message")
    var errorMessage: String? = null
)