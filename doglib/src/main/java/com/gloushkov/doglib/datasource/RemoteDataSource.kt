package com.gloushkov.doglib.datasource

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.gloushkov.doglib.model.Resource
import com.gloushkov.doglib.rest.ImageApi
import com.gloushkov.doglib.rest.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Ognian Gloushkov on 14.09.23.
 */
class RemoteDataSource {

    private val restService: ImageApi = RetrofitClient.apiService

    /**
     * This retrieves a random image url from the dog-ceo api.
     */
    suspend fun getImageUrl(): Flow<Resource<String>> = flow {
        try {
            val response = restService.getRandomImage().execute()

            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.isOk()) {
                        emit(
                            Resource.error(
                                null,
                                Resource.Error.ApiError(it.status)
                            )
                        )

                    } else {
                        emit(Resource.success(it.data))
                    }
                }
            } else {
                emit(
                    Resource.error(
                        null,
                        Resource.Error.HttpError(response.code())
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.error(null, Resource.Error.RuntimeException(e.fillInStackTrace())))
        }
    }

    suspend fun getRandomImages(count: Int) = flow {
        try {
            val response = restService.getRandomImages(count).execute()

            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.isOk()) {
                        emit(
                            Resource.error(
                                null,
                                Resource.Error.ApiError(it.status)
                            )
                        )

                    } else {
                        emit(Resource.success(it.data))
                    }
                }
            } else {
                emit(
                    Resource.error(
                        null,
                        Resource.Error.HttpError(response.code())
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.error(null, Resource.Error.RuntimeException(e.fillInStackTrace())))
        }
    }

    suspend fun downloadBitmap(url: String): Resource<Bitmap> = withContext(Dispatchers.IO) {
        suspendCoroutine {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                it.resume(Resource.success(bitmap))
            } catch (e: Exception) {
                it.resume(
                    Resource.error(
                        null,
                        Resource.Error.RuntimeException(e.fillInStackTrace())
                    )
                )
            }
        }
    }
}