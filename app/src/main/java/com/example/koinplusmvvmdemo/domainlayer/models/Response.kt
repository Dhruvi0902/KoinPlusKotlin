package com.example.koinplusmvvmdemo.domainlayer.models

import com.google.gson.annotations.SerializedName

class Response {

    @SerializedName("Search")
    var movies: List<Movie>? = null
    @SerializedName("totalResults")
    var totalResults: String? = null
    @SerializedName("Response")
    var response: String? = null

}
