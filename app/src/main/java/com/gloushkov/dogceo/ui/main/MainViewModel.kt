package com.gloushkov.dogceo.ui.main

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.NumberFormatException

private val TAG = "MainViewModel"
class MainViewModel : ViewModel() {


    private val _submitError = MutableLiveData<SubmitError?>()
    val submitError: LiveData<SubmitError?>
        get() = _submitError

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