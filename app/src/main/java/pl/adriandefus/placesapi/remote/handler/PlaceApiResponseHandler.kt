package pl.adriandefus.placesapi.remote.handler

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import pl.adriandefus.placesapi.remote.model.PlaceBaseResponse

fun <T : PlaceBaseResponse>Observable<T>.handleRxPlaceSearchResponse(): Observable<T> =
    this.flatMap { RxPlaceSearchResponseHandler<T>().apply(it) }


private class RxPlaceSearchResponseHandler<T : PlaceBaseResponse> : Function<T, ObservableSource<T>> {

    companion object {
        private const val ZERO_RESULTS = "ZERO_RESULTS"
        private const val OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT"
        private const val REQUEST_DENIED = "REQUEST_DENIED"
        private const val INVALID_REQUEST = "INVALID_REQUEST"
        private const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
    }

    override fun apply(response: T): ObservableSource<T> {
        return when (response.status) {
            ZERO_RESULTS -> Observable.error(PlaceSearchZeroResultError())
            OVER_QUERY_LIMIT -> Observable.error(PlaceSearchOverQueryError())
            REQUEST_DENIED -> Observable.error(PlaceSearchRequestDeniedError(response.errorMessage))
            INVALID_REQUEST -> Observable.error(PlaceSearchInvalidRequestError(response.errorMessage))
            UNKNOWN_ERROR -> Observable.error(PlaceSearchUnknownError(response.errorMessage))
            else -> Observable.just(response)
        }
    }
}

class PlaceSearchZeroResultError : Throwable()
class PlaceSearchOverQueryError : Throwable()
class PlaceSearchRequestDeniedError(message: String?) : Throwable(message)
class PlaceSearchInvalidRequestError(message: String?) : Throwable(message)
class PlaceSearchUnknownError(message: String?) : Throwable(message)