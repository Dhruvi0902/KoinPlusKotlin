package com.example.koinplusmvvmdemo.framework.dicomponents

import com.example.koinplusmvvmdemo.domainlayer.repositories.MovieRepository
import com.example.koinplusmvvmdemo.framework.TaskExecutors
import org.koin.dsl.module

val repositoryModule = module {
    single { TaskExecutors() }
    single { MovieRepository() }


}