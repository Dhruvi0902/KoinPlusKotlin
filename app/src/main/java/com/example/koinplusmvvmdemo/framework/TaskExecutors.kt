package com.example.koinplusmvvmdemo.framework

import android.os.Handler
import android.os.Looper
import org.koin.dsl.module

import java.util.concurrent.Executor
import java.util.concurrent.Executors

val taskExecutorsModule = module {
    factory { TaskExecutors() }
}

class TaskExecutors
/*
     * methods to dispatch tasks on demanded thread.*/ {
    val diskOperationThread: Executor
    val networkOperationThread: Executor
    val mainThread: Executor

    init {
        this.diskOperationThread = Executors.newSingleThreadExecutor()
        this.networkOperationThread = Executors.newFixedThreadPool(3)
        this.mainThread = MainThreadExecutor()
    }

    private inner class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}