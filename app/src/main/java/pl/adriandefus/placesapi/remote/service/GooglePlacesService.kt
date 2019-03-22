package pl.adriandefus.placesapi.remote.service

import io.reactivex.Observable
import pl.adriandefus.placesapi.remote.model.PlaceAutocompleteDto
import pl.adriandefus.placesapi.remote.model.PlaceDetailsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesService {

    @GET("autocomplete/json")
    fun getPlacesAutocomplete(
        @Query("input") input: String,
        @Query("key") appKey: String
    ): Observable<PlaceAutocompleteDto>

    @GET("details/json")
    fun getPlaceDetails(
        @Query("placeid") placeId: String,
        @Query("key") appKey: String
    ) : Observable<PlaceDetailsDto>
}