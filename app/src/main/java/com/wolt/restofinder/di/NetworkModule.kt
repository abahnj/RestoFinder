package com.wolt.restofinder.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wolt.restofinder.data.remote.api.WoltApi
import com.wolt.restofinder.data.remote.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ErrorInterceptor())
//        .apply {
//            if (com.wolt.restofinder.BuildConfig.DEBUG) {
//                addInterceptor(HttpLoggingInterceptor { message ->
//                    Timber.tag("OkHttp").d(message)
//                }.apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                })
//            }
//        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://restaurant-api.wolt.com/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideWoltApi(retrofit: Retrofit): WoltApi = retrofit.create(WoltApi::class.java)
}
