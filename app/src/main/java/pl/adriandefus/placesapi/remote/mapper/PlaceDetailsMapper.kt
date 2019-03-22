package pl.adriandefus.placesapi.remote.mapper

import com.google.android.gms.maps.model.LatLng
import pl.adriandefus.placesapi.domain.model.Place
import pl.adriandefus.placesapi.remote.model.PlaceDetailsDto
import javax.inject.Inject

class PlaceDetailsMapper @Inject constructor() : DtoMapper<PlaceDetailsDto, Place> {
    override fun mapFromRemote(dto: PlaceDetailsDto): Place {
        val latitude = dto.placeDetailsResult.placeGeometry.placeLocation.placeLatitude
        val longitude = dto.placeDetailsResult.placeGeometry.placeLocation.placeLongitude
        return Place(
            id = dto.placeDetailsResult.placeId,
            name = dto.placeDetailsResult.placeName,
            location = LatLng(latitude, longitude)
        )
    }
}
