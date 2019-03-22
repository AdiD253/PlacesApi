package pl.adriandefus.placesapi.domain.db

import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmResults
import pl.adriandefus.placesapi.domain.model.PlaceInformation

class RealmService(
    private val realm: Realm
) {

    fun addPlaceInformationToFavourites(
        placeInformation: PlaceInformation,
        addPlaceInformationCallback: AddPlaceInformationCallback
    ) {
        realm.executeTransactionAsync(
            {
                it.createObject(PlaceInformation::class.java, placeInformation.id).apply {
                    placeId = placeInformation.placeId
                    description = placeInformation.description
                    isFavourite = placeInformation.isFavourite
                }
            },
            {
                addPlaceInformationCallback.onAddPlaceInformationSuccess()
            },
            {
                addPlaceInformationCallback.onAddPlaceInformationError(it)
            }
        )
    }

    fun getAllPlacesInformationFlowable(): Flowable<List<PlaceInformation>> =
        getAllPlacesInformation().asFlowable().map { it.toList() }

    fun getAllPlacesInformation(): RealmResults<PlaceInformation> =
        realm.where(PlaceInformation::class.java).findAll().sort("description")

    fun removePlaceFromFavourites(
        placeInformation: PlaceInformation,
        removePlaceInformationCallback: RemovePlaceInformationCallback
    ) {
        realm.executeTransaction {
            placeInformation.isFavourite = false
            val results =
                it.where(PlaceInformation::class.java).equalTo("id", placeInformation.id).findAll()
            val deleteResult = results.deleteAllFromRealm()
            if (deleteResult) {
                removePlaceInformationCallback.onRemovePlaceInformationSuccess()
            } else {
                removePlaceInformationCallback.onRemovePlaceInformationError()
            }
        }
    }

    fun closeRealm() {
        realm.close()
    }
}

interface AddPlaceInformationCallback {
    fun onAddPlaceInformationSuccess()
    fun onAddPlaceInformationError(exception: Throwable)
}

interface RemovePlaceInformationCallback {
    fun onRemovePlaceInformationSuccess()
    fun onRemovePlaceInformationError()
}