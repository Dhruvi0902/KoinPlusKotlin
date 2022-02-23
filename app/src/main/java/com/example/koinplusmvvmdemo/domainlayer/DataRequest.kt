package com.example.koinplusmvvmdemo.domainlayer

/*
 * Wrapper class for Any type of request and response from any where like database or network.*/
class DataRequest<T> private constructor(val currentState: State, val data: T?, private val message: String?) {


    enum class State {
        LOADING,
        FAILED,
        SUCCEED
    }

    companion object {

        fun <T> succeed(data: T): DataRequest<T> {
            return DataRequest(State.SUCCEED, data, null)
        }

        fun <T> failed(msg: String, data: T?): DataRequest<T> {
            return DataRequest(State.FAILED, data, msg)
        }

        fun <T> loading(data: T?): DataRequest<T> {
            return DataRequest(State.LOADING, data, null)
        }
    }
}
