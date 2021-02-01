package com.dpycb.genericmovieapp.data.repo

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

class NetworkState(val status: Status, val message: String) {
    companion object{
        val LOADED: NetworkState
        val LOADING: NetworkState
        val ERROR: NetworkState
        val END: NetworkState

        init {
            LOADED = NetworkState(Status.SUCCESS, "Успешно!")
            LOADING = NetworkState(Status.RUNNING, "Загрузка")
            ERROR = NetworkState(Status.FAILED, "Ошибка!!!")
            END = NetworkState(Status.FAILED, "Вы достигли конца списка!")
        }
    }


}