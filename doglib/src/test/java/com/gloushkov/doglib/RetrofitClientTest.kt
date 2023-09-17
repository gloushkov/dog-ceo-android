package com.gloushkov.doglib

import com.gloushkov.doglib.rest.RetrofitClient
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Ognian Gloushkov on 16.09.23.
 */
class RetrofitClientTest {
    @Test
    fun testRetrofitInstance() {
        val instance = RetrofitClient.retrofit
        //Assert that, Retrofit's base url is not changed.
        assert(instance.baseUrl().url().toString() == "https://dog.ceo/api/")
    }
}