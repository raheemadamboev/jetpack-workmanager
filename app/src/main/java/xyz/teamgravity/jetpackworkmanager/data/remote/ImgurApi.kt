package xyz.teamgravity.jetpackworkmanager.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ImgurApi {

    companion object {
        const val BASE_URL = "https://i.imgur.com"
    }

    @GET("/X2qa7nQ.jpg")
    suspend fun downloadImage(): Response<ResponseBody>
}