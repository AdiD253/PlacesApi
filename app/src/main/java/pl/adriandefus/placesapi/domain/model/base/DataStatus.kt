package pl.adriandefus.placesapi.domain.model.base

class DataStatus<T>(
    val status: Status,
    var data: T? = null,
    var error: Throwable? = null
)

enum class Status {
    DEFAULT, PROGRESS, SUCCESS, ERROR
}