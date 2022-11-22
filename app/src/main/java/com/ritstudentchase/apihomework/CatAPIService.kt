package com.ritstudentchase.apihomework

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

const val BASE_URL = "https://api.thecatapi.com/"
const val API_KEY = "live_neW4SZXIDICFFcYixDdXqyKVx5EGAskRI22NOZuJUxDgT3pGYpvHeEDu9RdOw3Wv"

/**
 * Used to fetch cat information.
 */
interface CatAPIService {

    @GET("v1/images/search")
    suspend fun getRandomCatImage(): List<RandomCatImageModel>

    companion object {
        var apiService: CatAPIService? = null

        fun getInstance(): CatAPIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Handles Serialization and Deserialization automatically
                    .build()
                    .create(CatAPIService::class.java)
            }
            return apiService!!
        } // getInstance
    } // companion object
} // CatAPIService