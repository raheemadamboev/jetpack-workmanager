package xyz.teamgravity.jetpackworkmanager.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import xyz.teamgravity.jetpackworkmanager.data.remote.ImgurApi
import xyz.teamgravity.jetpackworkmanager.data.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideImgurApi(): ImgurApi =
        Retrofit.Builder()
            .baseUrl(ImgurApi.BASE_URL)
            .build()
            .create(ImgurApi::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: ImgurApi): Repository = Repository(api)
}