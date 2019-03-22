package pl.adriandefus.placesapi.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import pl.adriandefus.placesapi.domain.db.AddPlaceInformationCallback
import pl.adriandefus.placesapi.domain.db.RealmService
import pl.adriandefus.placesapi.domain.db.RemovePlaceInformationCallback
import pl.adriandefus.placesapi.domain.model.PlaceInformation
import pl.adriandefus.placesapi.domain.model.base.DataStatus
import pl.adriandefus.placesapi.domain.model.base.Status
import pl.adriandefus.placesapi.util.post
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(
    private val realmService: RealmService,
    private val disposable: CompositeDisposable
) : ViewModel() {

    companion object {
        private val ADD_PLACE_INFO_PROGRESS = DataStatus<Unit>(Status.PROGRESS)
        private val ADD_PLACE_INFO_SUCCESS = DataStatus<Unit>(Status.SUCCESS)
        private val ADD_PLACE_INFO_ERROR = DataStatus<Unit>(Status.ERROR)
        private val ADD_PLACE_INFO_DEFAULT = DataStatus<Unit>(Status.DEFAULT)

        private val REMOVE_PLACE_INFO_PROGRESS = DataStatus<Unit>(Status.PROGRESS)
        private val REMOVE_PLACE_INFO_SUCCESS = DataStatus<Unit>(Status.SUCCESS)
        private val REMOVE_PLACE_INFO_ERROR = DataStatus<Unit>(Status.ERROR)
        private val REMOVE_PLACE_INFO_DEFAULT = DataStatus<Unit>(Status.DEFAULT)
    }

    private val _addPlaceInformationStatus = MutableLiveData<DataStatus<Unit>>()
    val addPlaceInformationStatus: LiveData<DataStatus<Unit>>
        get() = _addPlaceInformationStatus

    private val _removePlaceInformationStatus = MutableLiveData<DataStatus<Unit>>()
    val removePlaceInformationStatus: LiveData<DataStatus<Unit>>
        get() = _removePlaceInformationStatus

    private val _favouritePlacesSizeStatus = MutableLiveData<DataStatus<Int>>()
    val favouritePlacesSizeStatus: LiveData<DataStatus<Int>>
        get() = _favouritePlacesSizeStatus

    fun addPlaceInformationToFavourites(placeInformation: PlaceInformation) {
        _addPlaceInformationStatus post ADD_PLACE_INFO_PROGRESS
        realmService.addPlaceInformationToFavourites(placeInformation, object : AddPlaceInformationCallback {
            override fun onAddPlaceInformationSuccess() {
                placeInformation.isFavourite = true
                _addPlaceInformationStatus post ADD_PLACE_INFO_SUCCESS
            }

            override fun onAddPlaceInformationError(exception: Throwable) {
                if (exception is RealmPrimaryKeyConstraintException) {
                    placeInformation.isFavourite = true
                    Log.d("KEYREALM", "realmprimarykeyexception occured")
                } else {
                    _addPlaceInformationStatus post ADD_PLACE_INFO_ERROR.apply {
                        error = exception
                    }
                }
            }
        })
    }

    fun removePlaceInformationFromFavourites(placeInformation: PlaceInformation) {
        _removePlaceInformationStatus post REMOVE_PLACE_INFO_PROGRESS
        realmService.removePlaceFromFavourites(placeInformation, object : RemovePlaceInformationCallback {
            override fun onRemovePlaceInformationSuccess() {
                _removePlaceInformationStatus post REMOVE_PLACE_INFO_SUCCESS
            }

            override fun onRemovePlaceInformationError() {
                _removePlaceInformationStatus post REMOVE_PLACE_INFO_ERROR
            }
        })
    }

    fun observeFavouritePlaces() {
        disposable +=
            realmService.getAllPlacesInformationFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _favouritePlacesSizeStatus post DataStatus(Status.SUCCESS, it.size)
                }
    }

    fun getFavouritePlaces(): RealmResults<PlaceInformation> = realmService.getAllPlacesInformation()

    override fun onCleared() {
        realmService.closeRealm()
        disposable.clear()
        _addPlaceInformationStatus post ADD_PLACE_INFO_DEFAULT
        _removePlaceInformationStatus post REMOVE_PLACE_INFO_DEFAULT
        super.onCleared()
    }
}