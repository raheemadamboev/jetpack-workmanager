package xyz.teamgravity.jetpackworkmanager.core.worker

import android.content.Context
import android.graphics.*
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import xyz.teamgravity.jetpackworkmanager.core.constant.Worker
import java.io.File
import java.io.FileOutputStream

@HiltWorker
@Suppress("BlockingMethodInNonBlockingContext")
class ColorFilterWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val file = parameters.inputData.getString(Worker.IMAGE_URI)?.toUri()?.toFile()
        delay(5_000L)

        return withContext(Dispatchers.IO) {
            return@withContext file?.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val resultBitmap = bitmap.copy(bitmap.config, true)

                val paint = Paint()
                paint.colorFilter = LightingColorFilter(0x08FF04, 1)
                val canvas = Canvas(resultBitmap)
                canvas.drawBitmap(resultBitmap, 0F, 0F, paint)

                val resultImageFile = File(context.filesDir, "new-image.jpg")
                val output = FileOutputStream(resultImageFile)
                val successful = resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)

                if (successful) Result.success(workDataOf(Worker.FILTER_URI to resultImageFile.toUri().toString()))
                else Result.failure()
            } ?: Result.failure()
        }
    }
}