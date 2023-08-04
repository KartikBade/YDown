package com.example.ydown.workmanager

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.ydown.R
import com.example.ydown.repositories.PythonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class DownloadWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val position = inputData.getInt("position", -1)
        Log.e("DownloadWorker", "Inside download worker: $position")
        startForegroundService()
        return withContext(Dispatchers.IO) {
            try {
                Log.e("DownloadWorker", "Inside try block")
                PythonRepository(context).downloadVideo(position)
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("DownloadWorker", it) }
                return@withContext Result.failure(
                    workDataOf(
                        "ERROR_MSG" to e.localizedMessage
                    )
                )
            }
            Result.success()
        }
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.y_down_icon)
                    .setContentTitle("Download in progress")
                    .setContentText("Downloading...")
                    .build()
            )
        )
    }
}