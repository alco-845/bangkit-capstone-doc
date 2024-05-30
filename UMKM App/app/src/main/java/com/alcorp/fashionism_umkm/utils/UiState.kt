package com.alcorp.fashionism_umkm.utils

data class UiState<T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): UiState<T> = UiState(Status.SUCCESS, data, null)

        fun <T> message(msg: String?): UiState<T> = UiState(Status.MESSAGE, null, msg)

        fun <T> loading(): UiState<T> = UiState(Status.LOADING, null, null)
    }
}

enum class Status {
    SUCCESS,
    MESSAGE,
    LOADING
}