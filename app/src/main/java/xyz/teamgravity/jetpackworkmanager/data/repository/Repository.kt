package xyz.teamgravity.jetpackworkmanager.data.repository

import okhttp3.ResponseBody
import retrofit2.Response
import xyz.teamgravity.jetpackworkmanager.data.remote.ImgurApi

class Repository(
    private val api: ImgurApi
) {

    suspend fun downloadImage(): Response<ResponseBody> {
        return api.downloadImage()
    }
}