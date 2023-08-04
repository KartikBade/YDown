package com.example.ydown.repositories

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class PythonRepository(context: Context) {

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context));
        }
    }

    private val py = Python.getInstance()
    private val module = py.getModule("youtubeVideoDownloaderScript")
    private val yt = module["yt"]
    private val download = module["download"]

    fun getQualityList(): List<PyObject>? {
        return yt?.call()?.asList()
    }

    fun downloadVideo(position: Int) {
        download?.call(position)
    }
}