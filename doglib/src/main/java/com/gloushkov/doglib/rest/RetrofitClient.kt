package com.gloushkov.doglib.rest

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
internal object RetrofitClient {
    private const val BASE_URL = "https://dog.ceo/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

    }
    val apiService: ImageApi = retrofit.create(ImageApi::class.java)
}