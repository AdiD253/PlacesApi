package pl.adriandefus.placesapi.remote.model

import com.google.gson.annotations.SerializedName

class PlaceDetailsDto(
    @SerializedName("result")
    val placeDetailsResult: PlaceDetailsResultDto
) : PlaceBaseResponse()

class PlaceDetailsResultDto(
    @SerializedName("id")
    val placeId: String,

    @SerializedName("name")
    val placeName: String,

    @SerializedName("geometry")
    val placeGeometry: PlaceGeometryDto
)

class PlaceGeometryDto(
    @SerializedName("location")
    val placeLocation: PlaceLocationDto
)

class PlaceLocationDto(
    @SerializedName("lat")
    val placeLatitude: Double,

    @SerializedName("lng")
    val placeLongitude: Double
)