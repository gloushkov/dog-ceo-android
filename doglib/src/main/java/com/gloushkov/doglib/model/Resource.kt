package com.gloushkov.doglib.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
class Resource<Type>(
    val status: Status = Status.IDLE,
    val data: Type?,
    val error: Error?
) {

    override fun equals(other: Any?): Boolean {
        if (other !is Resource<*>) return false
        if (this.status != other.status) return false
        if (this.data != other.data) return false
        return true
    }
    enum class Status {
        IDLE, LOADING, SUCCESS, ERROR
    }

    sealed class Error {
        object NoConnection: Error()
        data class HttpError(val code: Int): Error()
        data class ApiError(val message: String?) : Error()
        data class RuntimeException(val t: Throwable) : Error()
        object Unknown : Error()
    }

    companion object {
        /**
         * Helper method to create fresh state resource
         */
        fun <Type> success(@NonNull data: Type): Resource<Type> = Resource(Status.SUCCESS, data, null)
        /**
         * Helper method to create error state Resources. Error state might also have the current data, if any
         */
        fun <Type> error(@Nullable data: Type?, error: Error = Error.Unknown): Resource<Type> = Resource(
            Status.ERROR, data, error)
        /**
         * Helper method to create loading state Resources.
         */
        fun <Type> loading(@Nullable data: Type? = null): Resource<Type> = Resource(Status.LOADING, data, null)
    }
}