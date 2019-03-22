package pl.adriandefus.placesapi.remote.mapper

interface DtoMapper<in M, out E> {
    fun mapFromRemote(dto: M): E
}