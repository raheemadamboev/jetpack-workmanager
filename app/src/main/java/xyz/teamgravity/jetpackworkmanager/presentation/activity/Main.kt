package xyz.teamgravity.jetpackworkmanager.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.*
import coil.compose.rememberImagePainter
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.jetpackworkmanager.core.constant.Worker
import xyz.teamgravity.jetpackworkmanager.core.worker.ColorFilterWorker
import xyz.teamgravity.jetpackworkmanager.core.worker.DownloadWorker
import xyz.teamgravity.jetpackworkmanager.presentation.theme.JetpackWorkmanagerTheme

@AndroidEntryPoint
class Main : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        val colorFilterWorker = OneTimeWorkRequestBuilder<ColorFilterWorker>()
            .build()
        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            JetpackWorkmanagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val workInfos by remember(workManager) {
                        workManager.getWorkInfosForUniqueWorkLiveData("download")
                    }.observeAsState()

                    val downloadInfo = remember(workInfos) {
                        workInfos?.find { it.id == downloadWorker.id }
                    }

                    val filterInfo = remember(workInfos) {
                        workInfos?.find { it.id == colorFilterWorker.id }
                    }

                    val imageUri by derivedStateOf {
                        val downloadUri = downloadInfo?.outputData?.getString(Worker.IMAGE_URI)?.toUri()
                        val filterUri = filterInfo?.outputData?.getString(Worker.FILTER_URI)?.toUri()
                        filterUri ?: downloadUri
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        imageUri?.let { imageUri ->
                            Image(
                                painter = rememberImagePainter(data = imageUri),
                                contentDescription = "image",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Button(
                            onClick = {
                                workManager.beginUniqueWork("download", ExistingWorkPolicy.KEEP, downloadWorker)
                                    .then(colorFilterWorker)
                                    .enqueue()
                            },
                            enabled = downloadInfo?.state != WorkInfo.State.RUNNING
                        ) {
                            Text(text = "Start download")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        when (downloadInfo?.state) {
                            WorkInfo.State.RUNNING -> Text("Downloading...")
                            WorkInfo.State.SUCCEEDED -> Text("Download succeeded")
                            WorkInfo.State.FAILED -> Text("Download failed")
                            WorkInfo.State.CANCELLED -> Text("Download cancelled")
                            WorkInfo.State.ENQUEUED -> Text("Download enqueued")
                            WorkInfo.State.BLOCKED -> Text("Download blocked")
                            else -> Unit
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        when (filterInfo?.state) {
                            WorkInfo.State.RUNNING -> Text("Applying filter...")
                            WorkInfo.State.SUCCEEDED -> Text("Filter succeeded")
                            WorkInfo.State.FAILED -> Text("Filter failed")
                            WorkInfo.State.CANCELLED -> Text("Filter cancelled")
                            WorkInfo.State.ENQUEUED -> Text("Filter enqueued")
                            WorkInfo.State.BLOCKED -> Text("Filter blocked")
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}