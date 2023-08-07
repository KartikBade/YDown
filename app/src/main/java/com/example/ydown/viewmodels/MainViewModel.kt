package com.example.ydown.viewmodels

import androidx.appcompat.app.AppCompatActivity
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
    var link: String = ""

    fun getQualityList() {
        viewModelScope.launch {
            qualityList.value = pythonRepository.getQualityList(link)
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