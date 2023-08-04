package com.example.ydown.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.example.ydown.repositories.PythonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val pythonRepository: PythonRepository
): ViewModel() {

    var qualityList: MutableLiveData<List<PyObject>> = MutableLiveData()

    fun getQualityList() {
        viewModelScope.launch {
            qualityList.value = pythonRepository.getQualityList()
        }
    }

    fun downloadVideo(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            pythonRepository.downloadVideo(position)
        }
    }
}

class MainViewModelProviderFactory(
    private val pythonRepository: PythonRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(pythonRepository) as T
    }
}