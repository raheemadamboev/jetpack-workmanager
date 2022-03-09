package xyz.teamgravity.jetpackworkmanager.core.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import xyz.teamgravity.jetpackworkmanager.R
import xyz.teamgravity.jetpackworkmanager.core.constant.Notification
import xyz.teamgravity.jetpackworkmanager.core.constant.Worker
import xyz.teamgravity.jetpackworkmanager.data.repository.Repository
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@HiltWorker
@Suppress("BlockingMethodInNonBlockingContext")
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted parameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        startForeground()
        delay(5_000L)
        return withContext(Dispatchers.IO) {
            val response = repository.downloadImage()

            if (response.isSuccessful) {
                response.body()?.let { body ->

                    val file = File(context.filesDir, "image.jpg")
                    val output = FileOutputStream(file)

                    output.use { stream ->
                        try {
                            stream.write(body.bytes())
                        } catch (e: IOException) {
                            return@withContext Result.failure(workDataOf(Worker.ERROR to e.message))
                        }
                    }

                    return@withContext Result.success(workDataOf(Worker.IMAGE_URI to file.toUri().toString()))
                }
                return@withContext Result.failure()
            } else {
                if (response.code().toString().startsWith("5")) Result.retry()
                return@withContext Result.failure(workDataOf(Worker.ERROR to "Network error"))
            }
        }
    }

    private suspend fun startForeground() {
        setForeground(
            ForegroundInfo(
                Notification.NOTIFICATION_ID,
                NotificationCompat.Builder(context, Notification.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Downloading...")
                    .setContentText("Downloading in progress!")
                    .build()
            )
        )
    }
}