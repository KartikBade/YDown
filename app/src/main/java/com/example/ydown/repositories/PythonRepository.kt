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

    fun getQualityList(link: String): List<PyObject>? {
        return yt?.call(link)?.asList()
    }

    fun downloadVideo(link: String, position: Int, destination: String) {
        download?.call(link, position, destination)
    }
}