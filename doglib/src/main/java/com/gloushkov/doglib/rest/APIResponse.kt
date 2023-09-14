package com.gloushkov.doglib.rest

import com.fasterxml.jackson.annotation.JsonProperty
/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
internal data class APIResponse<Type>(
    @JsonProperty("status")
    val status: String,
    @JsonProperty("message")
    val data: Type
) {
    fun isOk() = status == ImageApi.STATUS_OK
}