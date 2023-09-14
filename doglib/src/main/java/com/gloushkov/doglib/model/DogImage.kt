package com.gloushkov.doglib.model

import android.graphics.Bitmap

/**
 * Created by Ognian Gloushkov on 11.09.23.
 */
internal data class DogImage(
    val imageUri: String,
    val bitmap: Bitmap?
)
