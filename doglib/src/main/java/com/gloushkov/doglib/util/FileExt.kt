package com.gloushkov.doglib.util

import android.graphics.Bitmap
import java.io.File

/**
 * Created by Ognian Gloushkov on 12.09.23.
 */

fun File.createFileAndDirs() = apply {
    parentFile?.mkdirs()
    createNewFile()
}
fun File.writeBitmap(
    bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100
) = apply {
    createFileAndDirs()
    outputStream().use {
        bitmap.compress(format, quality, it)
        it.flush()
    }
}