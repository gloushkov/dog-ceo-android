package com.gloushkov.dogceo.ui.main

import android.app.Application
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

class ListViewModel(private val application: Application) : AndroidViewModel(application) {
    private val service: IDogLib = DogLib

    private val _data = MutableLiveData<Resource<List<String>>>()
    val data: LiveData<Resource<List<String>>>
        get() = _data

    private val observer = FlowCollector<Resource<List<String>>> {
        when (it.status) {
            IDLE -> {}
            LOADING -> {
                _data.postValue(Resource.loading())
            }

            SUCCESS -> {
                _data.postValue(Resource.success(it.data!!))
            }

            ERROR -> {
                _data.postValue(Resource.error(null, it.error!!))
            }
        }
    }

    fun loadImages(count: Int) {
        viewModelScope.launch {
            service.getImages(application.applicationContext, count).collect(observer)
        }
    }
}