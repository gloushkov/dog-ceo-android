package com.gloushkov.doglib

import android.content.Context
import android.graphics.Bitmap
import com.gloushkov.doglib.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
interface IDogLib {
    /**
     * Gets one image of a dog from the library.
     * @param context [Context] is used to read/write the [Bitmap] as a [File] in the cache directory.
     */
    suspend fun getImage(context: Context): Flow<Resource<Bitmap>>
    /**
     * Gets the number of dog images mentioned in the method
     * @param context [Context] is used to read/write the [Bitmap]s as [File]s in the cache directory.
     * @param count the number of [Bitmap]s to be returned.
     */
    suspend fun getImages(context: Context, count: Int): List<Bitmap>

    /**
     * Gets the next image of a dog
     * @param context [Context] is used to read/write the [Bitmap] as a [File] in the cache directory.
     */
    suspend fun getNextImage(context: Context): Flow<Resource<Bitmap>>

    /**
     * Gets the previous image of a dog
     * @param context [Context] is used to read/write the [Bitmap] as a [File] in the cache directory.
     */
    suspend fun getPreviousImage(context: Context): Flow<Resource<Bitmap>>
}