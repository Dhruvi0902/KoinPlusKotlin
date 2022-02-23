package com.example.koinplusmvvmdemo.domainlayer.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.koinplusmvvmdemo.domainlayer.DataRequest
import com.example.koinplusmvvmdemo.framework.TaskExecutors

/*
 * Hero of our application. It performs whole flow of data.
 * What to do after something.*/
abstract class DataManager<RequestType, ResultType> protected constructor(private val taskExecutors: TaskExecutors) {

    private val result: MediatorLiveData<DataRequest<ResultType>> = MediatorLiveData()

    init {
        result.value = DataRequest.loading(null)
        val sourceDatabase = loadFromDatabase()
        result.addSource(sourceDatabase) { data ->
            result.removeSource(sourceDatabase)
            if (shouldFetchData(data)) {
                fetchFromNetwork(sourceDatabase)
            } else {
                result.addSource(sourceDatabase) { newDataFromDatabase -> setValue(DataRequest.succeed(newDataFromDatabase)) }
            }

        }
    }

    /*
     * This method works asynchronously by Room's own implementation.*/
    protected abstract fun loadFromDatabase(): LiveData<ResultType>

    /*
     * This method works asynchronously by RxPatten.*/
    protected abstract fun loadFromNetwork(): LiveData<RequestType>?

    protected abstract fun shouldFetchData(data: ResultType?): Boolean

    protected abstract fun saveDataToDatabase(data: ResultType)

    protected abstract fun clearPreviousData()

    fun onFetchFailed(throwable: Throwable) {
        setValue(DataRequest.failed(throwable.localizedMessage, null))
    }

    /*
     * Updates the live data which we are interested in.*/
    private fun setValue(newValue: DataRequest<ResultType>) {
        if (result.value !== newValue) {
            result.value = newValue
        }
    }

    fun toLiveData(): LiveData<DataRequest<ResultType>> {
        return result
    }

    protected abstract fun processResponse(response: RequestType): ResultType?

    private fun fetchFromNetwork(sourceDatabase: LiveData<ResultType>) {
        val sourceNetwork = loadFromNetwork()
        result.addSource(sourceDatabase
        ) { dataFromDatabase ->
            /*setValue(DataRequest.loading(dataFromDatabase)));*/
            setValue(DataRequest.loading(null))
        }
        result.addSource(sourceNetwork!!
        ) { dataFromNetwork ->
            result.removeSource(sourceNetwork)
            result.removeSource(sourceDatabase)
            taskExecutors.diskOperationThread.execute {
                val processedData = processResponse(dataFromNetwork)
                if (processedData == null) {
                    taskExecutors.mainThread.execute { setValue(DataRequest.failed("Not Found", null)) }
                    return@execute
                }

                clearPreviousData()
                saveDataToDatabase(processedData)
                taskExecutors.mainThread.execute {
                    result.addSource(loadFromDatabase()
                    ) { newDataFromDatabase -> setValue(DataRequest.succeed(newDataFromDatabase)) }
                }
            }

            /*
                     * Here we can handle various type of responses we can get form network,
                     * whether it can be empty or some error on page or anything like that.*/

            /*switch (dataFromNetwork.getCurrentState()) {
                        case SUCCEED: {
                            taskExecutors.getDiskOperationThread().execute(() ->
                            {
                                saveDataToDatabase(processResponse(dataFromNetwork));
                                taskExecutors.getMainThread().execute(() ->
                                        result.addSource(loadFromDatabase(),
                                                newDataFromDatabase ->
                                                        setValue(DataRequest.succeed(newDataFromDatabase))));
                            });
                        }
                        break;
                        case EMPTY: {
                            taskExecutors.getMainThread().execute(() -> result.addSource(loadFromDatabase(),newDataFromDatabase -> setValue(DataRequest.succeed(newDataFromDatabase))));
                        }
                        break;
                        case ERROR: {
                            onFetchFailed();
                            result.addSource(sourceDatabase,newDataFromDatabase -> setValue(DataRequest.failed("Error Message",newDataFromDatabase)));

                        }
                        break;
                    }*/
        }
    }
}
