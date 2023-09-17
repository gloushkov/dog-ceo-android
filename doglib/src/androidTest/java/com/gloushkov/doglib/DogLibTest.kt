package com.gloushkov.doglib

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gloushkov.doglib.model.Resource
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

/**
 * Created by Ognian Gloushkov on 16.09.23.
 */
private const val TAG = "DogLibTest"
@RunWith(AndroidJUnit4::class)
class DogLibTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext;

    private val dogLib = DogLib

    private val imageRepository = mockk<ImageRepository>()


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.gloushkov.doglib.test", appContext.packageName)
    }

    @Test
    fun getImage(): Unit = runBlocking {
        Log.v(TAG, "assert empty list")
        assert(dogLib.images.size == 0)
        dogLib.getImage(context).collectIndexed { index, value ->
            if (index == 0 || index == 1) {
                Log.v(TAG, "assert Resource.Status.LOADING")
                assert(value.status == Resource.Status.LOADING)
            } else if (index == 2) {
                Log.v(TAG, "assert Resource.Status.SUCCESS")
                assert(value.status == Resource.Status.SUCCESS)
            }
        }
        Log.v(TAG, "assert one item in list")
        assert(dogLib.images.size == 1)
    }

    @Test
    fun getImages() {

    }

    @Test
    fun getNextImage() = runBlocking {
        dogLib.getNextImage(context).collectIndexed { index, value ->
            if (index == 0 || index == 1) {
                assert(value.status == Resource.Status.LOADING)
            } else if (index == 2) {
                assert(value.status == Resource.Status.SUCCESS)
            }
        }
        assert(dogLib.images.size == 1)
    }

    @Test
    fun getPreviousImage() {
    }
}