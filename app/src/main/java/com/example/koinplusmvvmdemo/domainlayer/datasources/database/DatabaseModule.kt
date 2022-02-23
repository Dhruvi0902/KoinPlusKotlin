package com.example.koinplusmvvmdemo.domainlayer.datasources.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.koinplusmvvmdemo.domainlayer.models.Movie

class DatabaseModule(internal var context: Context) {

    @Dao
    interface MovieDao {

        @get:Query("SELECT * from movie")
        val movieList: LiveData<List<Movie>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(movieList: List<Movie>): LongArray

        @Query("SELECT * from movie WHERE imdbID = :id")
        fun getMovie(id: String): LiveData<Movie>

        @Query("DELETE FROM movie")
        fun removeAllMovies()
    }

    @Database(entities = [Movie::class], version = 1, exportSchema = false)
    abstract class MovieDatabase : RoomDatabase() {
        abstract fun movieDao(): MovieDao
    }


}