package pl.adriandefus.placesapi.remote.mapper

import pl.adriandefus.placesapi.domain.model.PlaceInformation
import pl.adriandefus.placesapi.remote.model.PlaceAutocompleteDto
import javax.inject.Inject

class PlaceInformationMapper @Inject constructor() : DtoMapper<PlaceAutocompleteDto, List<PlaceInformation>> {

    override fun mapFromRemote(dto: PlaceAutocompleteDto): List<PlaceInformation> =
        dto.predictions.map {
            PlaceInformation(
                id = it.id,
                placeId = it.placeId,
                description = it.description
            )
        }
}