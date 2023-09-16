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
        if (validateSubmitInput(input)) {
            //TODO call library
        }
    }

    @VisibleForTesting
    fun validateSubmitInput(input: String): Boolean {
        Log.v(TAG, "validateSubmitInput($input)")
        val count = try {
            Integer.parseInt(input)
        } catch (nfe: NumberFormatException) {
            Log.w(TAG, "NumberFormatException")
            _submitError.postValue(SubmitError.NOT_A_NUMBER)
            return false
        }
        Log.v(TAG, "validateSubmitInput - count = $count")
        if (count < 1) {
            Log.w(TAG, "validateSubmitInput - SubmitError.BELLOW_RANGE")
            _submitError.postValue(SubmitError.BELLOW_RANGE)
            return false;
        } else if (count > 10) {
            Log.w(TAG, "validateSubmitInput - SubmitError.ABOVE_RANGE")
            _submitError.postValue(SubmitError.ABOVE_RANGE)
            return false;
        }
        Log.d(TAG, "validateSubmitInput - input valid")
        _submitError.postValue(null)
        return true
    }

    enum class SubmitError {
        NOT_A_NUMBER,
        BELLOW_RANGE,
        ABOVE_RANGE,
        RESOURCE_ERROR
    }
}