package com.gloushkov.doglib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.gloushkov.doglib.datasource.RemoteDataSource
import com.gloushkov.doglib.model.DogImage
import com.gloushkov.doglib.model.Resource
import com.gloushkov.doglib.model.Resource.Status.ERROR
import com.gloushkov.doglib.model.Resource.Status.IDLE
import com.gloushkov.doglib.model.Resource.Status.LOADING
import com.gloushkov.doglib.model.Resource.Status.SUCCESS
import com.gloushkov.doglib.util.writeBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */

private const val TAG = "ImageRepository"

internal class ImageRepository {

    private val remoteDataSource = RemoteDataSource()
    suspend fun getRandomImage(context: Context, uuid: String): Flow<Pair<String, Resource<DogImage>>> = flow {
        // Call the API - get the next random image.
        // Afterwards we need to decide if we have that one saved or we need to load the bitmap from upstream as well
        emit(Pair(uuid, Resource.loading(null)))
        remoteDataSource.getImageUrl()
            .flowOn(Dispatchers.IO)
            .catch {
                Log.e(TAG, "FlowCollector error: $it")
                emit(Pair(uuid, Resource.error(null, Resource.Error.RuntimeException(it))))
            }
            .collect {
                when (it.status) {
                    IDLE -> {}
                    LOADING -> {}
                    SUCCESS -> {
                        //Notifying the library that we have received an image URL
                        emit(Pair(uuid, Resource.loading(DogImage(it.data!!, null))))

                        //Check cache or download the image
                        val image = provideBitmap(context, it.data)
                        emit(Pair(uuid, image))
                    }

                    ERROR -> {
                        Log.e(TAG, "API Error. ${it.error!!}")
                        emit(Pair(uuid, Resource.error(null, Resource.Error.Unknown)))
                    }
                }
            }
    }

    private suspend fun provideBitmap(
        context: Context,
        url: String
    ): Resource<DogImage> {

        //TODO LRU cache check should be here.

        val fileCache = File(context.cacheDir, url)
        if (fileCache.exists()) {
            return try {
                val bitmap = BitmapFactory.decodeStream(fileCache.inputStream());
                Resource.success(DogImage(url, bitmap))
            } catch (e: Exception) {
                Resource.error(
                    DogImage(url, null),
                    Resource.Error.RuntimeException(e.fillInStackTrace())
                )
            }
        }
        val downloadedImage = remoteDataSource.downloadBitmap(url)
        return if (downloadedImage.status == SUCCESS) {
            val image = DogImage(url, downloadedImage.data!!)
            cacheImage(context, image)
            Resource.success(image)
        } else {
            Resource.error(DogImage(url, null), downloadedImage.error!!)
        }
    }

    private fun cacheImage(context: Context, image: DogImage) =
        CoroutineScope(Dispatchers.IO).launch {
            image.bitmap?.let {
                File(context.cacheDir, image.imageUri).writeBitmap(
                    it,
                    Bitmap.CompressFormat.PNG,
                    100
                )
            }
        }
}
