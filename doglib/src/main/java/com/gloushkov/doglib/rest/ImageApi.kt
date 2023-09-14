package com.gloushkov.doglib.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
internal interface ImageApi {
    companion object {
        const val STATUS_OK = "success"
    }
    @GET("breeds/image/random")
    fun getRandomImage(): Call<APIResponse<String>>

    @GET("breeds/image/random/{count}")
    fun getRandomImages(@Path("count") count: Int): Call<APIResponse<List<String>>>
}