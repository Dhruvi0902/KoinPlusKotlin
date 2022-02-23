package com.example.koinplusmvvmdemo.domainlayer.datasources.network

import androidx.lifecycle.MutableLiveData
import com.example.koinplusmvvmdemo.domainlayer.DataRequest
import com.example.koinplusmvvmdemo.domainlayer.managers.DataManager
import com.example.koinplusmvvmdemo.domainlayer.models.Response
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class NetworkOperationObserver(internal var responseFromNetwork: MutableLiveData<DataRequest<Response>>, internal var dataManager: DataManager<*, *>) : Observer<Response> {

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(response: Response) {
        responseFromNetwork.value = DataRequest.succeed(response)
    }

    override fun onError(e: Throwable) {
        this.responseFromNetwork.value = DataRequest.failed(e.localizedMessage, null)
        dataManager.onFetchFailed(e)
    }

    override fun onComplete() {

    }
}
