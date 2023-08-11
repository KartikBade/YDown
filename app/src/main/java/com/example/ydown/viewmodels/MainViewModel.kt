package com.example.ydown.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.example.ydown.repositories.PythonRepository
import kotlinx.coroutines.*

enum class Status { INITIAL, LOADING, DONE, ERROR }

class MainViewModel(
    private val pythonRepository: PythonRepository
): ViewModel() {

    val status = MutableLiveData(Status.INITIAL)
    val qualityList: MutableLiveData<List<PyObject>> = MutableLiveData()
    var link: String = ""

    fun getQualityList() {
        viewModelScope.launch {
            try {
                status.value = Status.LOADING
                qualityList.value = pythonRepository.getQualityList(link)
                status.value = Status.DONE
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("MainViewModel", it) }
                status.value = Status.ERROR
            }
        }
    }

    fun checkForIntents(link: String) {
        this.link = link
        getQualityList()
    }
}

class MainViewModelProviderFactory(
    private val pythonRepository: PythonRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(pythonRepository) as T
    }
}