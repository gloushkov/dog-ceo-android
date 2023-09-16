package com.gloushkov.dogceo.ui.main

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gloushkov.doglib.DogLib
import com.gloushkov.doglib.IDogLib
import com.gloushkov.doglib.model.Resource
import com.gloushkov.doglib.model.Resource.Status.ERROR
import com.gloushkov.doglib.model.Resource.Status.IDLE
import com.gloushkov.doglib.model.Resource.Status.LOADING
import com.gloushkov.doglib.model.Resource.Status.SUCCESS
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"
class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val service: IDogLib = DogLib

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _previousButtonEnabled = MutableLiveData<Boolean>(false)
    val previousButtonEnabled: LiveData<Boolean>
        get() = _previousButtonEnabled

    private val _currentBitmap = MutableLiveData<Bitmap?>(null)
    val currentBitmap: LiveData<Bitmap?>
        get() = _currentBitmap

    private val _submitError = MutableLiveData<SubmitError?>(null)
    val submitError: LiveData<SubmitError?>
        get() = _submitError

    private val _redirectToList = MutableLiveData<Int>()
    val redirectToList: LiveData<Int>
        get() = _redirectToList

    private val observer = FlowCollector<Resource<Bitmap>> {
        Log.v(TAG, "getImageObserver: ${it.status}")
        when (it.status) {
            IDLE -> {
                _loading.postValue(false)
                _currentBitmap.postValue(null)
            }
            LOADING -> {
                Log.d(TAG, "Loading image")
                _loading.postValue(true)
                _currentBitmap.postValue(null)
            }
            SUCCESS -> {
                Log.d(TAG, "Received image")
                _loading.postValue(false)
                _previousButtonEnabled.postValue(true)
                _currentBitmap.postValue(it.data!!)
            }
            ERROR -> {
                Log.e(TAG, "Loading image error: ${it.error}")
                _loading.postValue(false)
                _previousButtonEnabled.postValue(true)
                _currentBitmap.postValue(null)
            }
        }
    }

    init {
        viewModelScope.launch {
            service.getImage(application.applicationContext).collect(observer)
        }
    }

    fun onNext() {
        viewModelScope.launch {
            service.getNextImage(application.applicationContext).collect(observer)
        }
    }

    fun onPrevious() {
        viewModelScope.launch {
            service.getPreviousImage(application.applicationContext).collect(observer)
        }
    }

    fun onSubmit(input: String) {
        val validInput = validateSubmitInput(input)
        if (validInput != -1) {
            _redirectToList.postValue(validInput)
        }
    }

    @VisibleForTesting
    fun validateSubmitInput(input: String): Int {
        Log.v(TAG, "validateSubmitInput($input)")
        val count = try {
            Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            Log.w(TAG, "NumberFormatException")
            _submitError.postValue(SubmitError.NOT_A_NUMBER)
            return -1
        }
        Log.v(TAG, "validateSubmitInput - count = $count")
        if (count < 1) {
            Log.w(TAG, "validateSubmitInput - SubmitError.BELLOW_RANGE")
            _submitError.postValue(SubmitError.BELLOW_RANGE)
            return -1;
        } else if (count > 10) {
            Log.w(TAG, "validateSubmitInput - SubmitError.ABOVE_RANGE")
            _submitError.postValue(SubmitError.ABOVE_RANGE)
            return -1;
        }
        Log.d(TAG, "validateSubmitInput - input valid")
        _submitError.postValue(null)
        return count
    }

    enum class SubmitError {
        NOT_A_NUMBER,
        BELLOW_RANGE,
        ABOVE_RANGE,
        RESOURCE_ERROR
    }
}