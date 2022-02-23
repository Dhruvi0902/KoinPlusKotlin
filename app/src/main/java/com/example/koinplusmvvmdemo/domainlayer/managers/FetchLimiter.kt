package com.example.koinplusmvvmdemo.domainlayer.managers


import android.os.SystemClock
import java.util.*
import java.util.concurrent.TimeUnit

/*
 * Stores what request we executed and when.*/
class FetchLimiter<KEY>(timeOut: Int, timeUnit: TimeUnit) {
    private val timestamps: MutableMap<KEY, Long>
    private val timeOut: Long?

    init {

        timestamps = HashMap()
        this.timeOut = timeUnit.toMillis(timeOut.toLong())
    }

    /*
     * Decides whether we should execute network request or not
     * on basis of time.*/
    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > this.timeOut!!) {
            timestamps[key] = now
            return true
        }
        return false
    }

    private fun now(): Long {
        return SystemClock.uptimeMillis()
    }

    @Synchronized
    internal fun reset(key: KEY) {
        timestamps.remove(key)
    }
}
