package com.example.koinplusmvvmdemo.clientlayer.presentations.movie

import androidx.lifecycle.LifecycleOwner

interface ViewController {
    val lifeCycleOwner: LifecycleOwner
    fun onErrorOccurred(message: String)

    fun onSucceed()

    fun onLoadingOccurred()
}
