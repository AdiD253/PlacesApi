package pl.adriandefus.placesapi.domain.model

import com.google.android.gms.maps.model.LatLng

data class Place(
    var id: String,
    var name: String,
    var location: LatLng
)