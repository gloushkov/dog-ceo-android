package com.gloushkov.doglib

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.gloushkov.doglib.model.DogImage
import com.gloushkov.doglib.model.Resource
import com.gloushkov.doglib.model.Resource.Status.ERROR
import com.gloushkov.doglib.model.Resource.Status.IDLE
import com.gloushkov.doglib.model.Resource.Status.LOADING
import com.gloushkov.doglib.model.Resource.Status.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
private const val TAG = "DogLib"

object DogLib : IDogLib {

    private val imageRepository = ImageRepository()

    //TODO refactor so that the Bitmap is not stored in RAM. Replace with LRU cache and keep track only of urls.
    private val images: ArrayList<Pair<String, Resource<DogImage>>> = arrayListOf()

    private val mutex = Mutex()

    private var currentIndex = 0
    override suspend fun getImage(context: Context): Flow<Resource<Bitmap>> = flow {
        val uuid = UUID.randomUUID().toString()
        imageRepository.getRandomImage(context, uuid).collect { result ->
            when (result.second.status) {
                IDLE -> {}
                LOADING -> {
                    mutex.withLock {
                        if (result.second.data == null) {
                            images.add(Pair(uuid, result.second))
                        }
                    }
                    emit(Resource.loading())
                }

                SUCCESS -> {
                    mutex.withLock {
                        val index =
                            images.indexOfFirst { it.first == uuid }
                        if (index != -1) {
                            val newPair = Pair(uuid, result.second)
                            images[index] = newPair
                            if (index == currentIndex) {
                                emit(Resource.success(result.second.data!!.bitmap!!))
                            } else {
                                Log.d(TAG, "Not current index. Skipping update.")
                            }
                        } else {
                            Log.e(TAG, "Resource not found.")
                        }
                    }
                }

                ERROR -> {
                    mutex.withLock {

                        val index =
                            images.indexOfFirst { it.first == uuid }
                        if (index != -1) {
                            val newPair = Pair(uuid, result.second)
                            images[index] = newPair
                            if (index == currentIndex) {
                                emit(Resource.error(null, result.second.error!!))
                            } else {
                                Log.d(TAG, "Not current index. Skipping update.")
                            }
                        } else {
                            Log.e(TAG, "Resource not found.")
                        }
                    }
                }
            }
        }
    }


    override suspend fun getImages(context: Context, count: Int): Flow<Resource<List<String>>> =
        flow {
            imageRepository.getRandomImages(count).flowOn(Dispatchers.IO)
                .catch {
                    emit(Resource.error(null, Resource.Error.RuntimeException(it)))
                }.collect {
                    emit(it as Resource<List<String>>)
                }
        }

    override suspend fun getNextImage(context: Context): Flow<Resource<Bitmap>> = flow {
        if (currentIndex == images.size - 1) {
            Log.d(TAG, "At end of list. Loading new.")
            currentIndex++
            getImage(context).collect {
                emit(it)
            }
        } else {
            val resource = images[++currentIndex].second
            when (resource.status) {
                IDLE -> {}
                LOADING -> emit(Resource.loading())
                SUCCESS -> emit(Resource.success(resource.data!!.bitmap!!))
                ERROR -> emit(Resource.error(null, resource.error!!))
            }
        }
    }

    override suspend fun getPreviousImage(context: Context): Flow<Resource<Bitmap>> = flow {
        if (currentIndex == 0 && images.size > 0) {
            currentIndex = images.size
        }
        val resource = images[--currentIndex].second
        when (resource.status) {
            IDLE -> {}
            LOADING -> emit(Resource.loading())
            SUCCESS -> emit(Resource.success(resource.data!!.bitmap!!))
            ERROR -> emit(Resource.error(null, resource.error!!))
        }
    }
}