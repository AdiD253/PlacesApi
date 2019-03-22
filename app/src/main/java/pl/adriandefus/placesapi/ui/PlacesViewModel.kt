package pl.adriandefus.placesapi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.adriandefus.placesapi.domain.model.Place
import pl.adriandefus.placesapi.domain.model.PlaceInformation
import pl.adriandefus.placesapi.domain.model.base.DataStatus
import pl.adriandefus.placesapi.domain.model.base.Status
import pl.adriandefus.placesapi.remote.handler.handleRxPlaceSearchResponse
import pl.adriandefus.placesapi.remote.mapper.PlaceDetailsMapper
import pl.adriandefus.placesapi.remote.mapper.PlaceInformationMapper
import pl.adriandefus.placesapi.remote.service.GooglePlacesService
import pl.adriandefus.placesapi.util.post
import javax.inject.Inject
import javax.inject.Named

class PlacesViewModel @Inject constructor(
    private val googlePlacesService: GooglePlacesService,
    @Named("API_KEY") val appKey: String,
    private val disposable: CompositeDisposable,
    private val placeInformationMapper: PlaceInformationMapper,
    private val placeDetailsMapper: PlaceDetailsMapper
) : ViewModel() {

    companion object {
        private val PLACE_AUTOCOMPLETE_PROGRESS = DataStatus<List<PlaceInformation>>(Status.PROGRESS)
        private val PLACE_AUTOCOMPLETE_ERROR = DataStatus<List<PlaceInformation>>(Status.ERROR)
        private val PLACE_AUTOCOMPLETE_SUCCESS = DataStatus<List<PlaceInformation>>(Status.SUCCESS)
        private val PLACE_AUTOCOMPLETE_DEFAULT = DataStatus<List<PlaceInformation>>(Status.DEFAULT)

        private val PLACE_INFO_PROGRESS = DataStatus<Place>(Status.PROGRESS)
        private val PLACE_INFO_ERROR = DataStatus<Place>(Status.ERROR)
        private val PLACE_INFO_SUCCESS = DataStatus<Place>(Status.SUCCESS)
        private val PLACE_INFO_DEFAULT = DataStatus<Place>(Status.DEFAULT)
    }

    private val _placeAutoCompleteStatus = MutableLiveData<DataStatus<List<PlaceInformation>>>()
    val placeAutoCompleteStatus: LiveData<DataStatus<List<PlaceInformation>>>
        get() = _placeAutoCompleteStatus

    private var lastAutocompleteQuery = ""

    private val _placeInfoStatus = MutableLiveData<DataStatus<Place>>()
    val placeInfoStatus: LiveData<DataStatus<Place>>
        get() = _placeInfoStatus

    fun performAutocompleteForQuery(query: String) {
        if (query.isEmpty()) {
            _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_SUCCESS.apply {
                data = listOf()
            }
        } else if (query != lastAutocompleteQuery) {
            disposable +=
                googlePlacesService.getPlacesAutocomplete(query, appKey)
                    .handleRxPlaceSearchResponse()
                    .map { placeInformationMapper.mapFromRemote(it) }
                    .doOnSubscribe {
                        _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_PROGRESS
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onError = {
                            _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_ERROR.apply {
                                error = it
                            }
                        },
                        onNext = {
                            lastAutocompleteQuery = query
                            _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_SUCCESS.apply {
                                data = it
                            }
                        }
                    )
        }
    }

    fun getPlaceForId(placeId: String) {
        disposable +=
            googlePlacesService.getPlaceDetails(placeId, appKey)
                .handleRxPlaceSearchResponse()
                .map { placeDetailsMapper.mapFromRemote(it) }
                .doOnSubscribe {
                    _placeInfoStatus post PLACE_INFO_PROGRESS
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {
                        _placeInfoStatus post PLACE_INFO_ERROR.apply {
                            error = it
                        }
                    },
                    onNext = {
                        _placeInfoStatus post PLACE_INFO_SUCCESS.apply {
                            data = it
                        }
                    }
                )
    }

    fun clearPlaceStatus() {
        _placeInfoStatus post PLACE_INFO_DEFAULT
    }

    fun clearAutocompleteStatus() {
        _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_DEFAULT
    }

    override fun onCleared() {
        disposable.clear()
        _placeInfoStatus post PLACE_INFO_DEFAULT
        _placeAutoCompleteStatus post PLACE_AUTOCOMPLETE_DEFAULT
        super.onCleared()
    }
}