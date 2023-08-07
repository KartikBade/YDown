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
import java.io.IOException
import kotlin.random.Random

class DownloadWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val link = inputData.getString("link")
        val position = inputData.getInt("position", -1)
        val destination = inputData.getString("destination")

        startForegroundService()
        return withContext(Dispatchers.IO) {
            try {
                if (link != null && destination != null) {
                    PythonRepository(context).downloadVideo(link, position, destination)
                } else {
                    throw IOException("Link or Destination was null.")
                }
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