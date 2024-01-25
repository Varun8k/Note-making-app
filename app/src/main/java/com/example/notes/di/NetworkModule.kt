package com.example.notes.di

import com.example.notes.api.NoteApi
import com.example.notes.api.OkhttpInterceptor
import com.example.notes.api.UserApi
import com.example.notes.utils.Constant.Base_Url
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Base_Url)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkhttpclint(okhttpInterceptor: OkhttpInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder().addInterceptor(okhttpInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideUserapi(retofit: Retrofit): UserApi = retofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideNoteApi(okHttpClient: OkHttpClient): NoteApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Base_Url)
            .build().create(NoteApi::class.java)
    }
}
