package com.example.koinplusmvvmdemo.framework.dicomponents

import androidx.room.Room
import com.example.koinplusmvvmdemo.MvvmKotlinPlusKoinApplication
import com.example.koinplusmvvmdemo.domainlayer.datasources.database.DatabaseModule
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            (androidApplication() as MvvmKotlinPlusKoinApplication),
            DatabaseModule.MovieDatabase::class.java,
            "movie_database.db"
        )
            .build()
    }
    single { (get() as DatabaseModule.MovieDatabase).movieDao() }


}
