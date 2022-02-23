package com.example.koinplusmvvmdemo

import android.app.Application
import com.example.koinplusmvvmdemo.framework.dicomponents.databaseModule
import com.example.koinplusmvvmdemo.framework.dicomponents.networkModuleDi
import com.example.koinplusmvvmdemo.framework.dicomponents.repositoryModule
import com.example.koinplusmvvmdemo.framework.dicomponents.viewModelModuledi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MvvmKotlinPlusKoinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin context
        startKoin {
            androidLogger()
            androidContext(this@MvvmKotlinPlusKoinApplication)
            modules(
                listOf(
                    networkModuleDi,
                    viewModelModuledi,
                    databaseModule,
                    repositoryModule
                )
            )
        }
    }

}