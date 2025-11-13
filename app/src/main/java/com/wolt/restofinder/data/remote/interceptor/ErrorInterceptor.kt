package com.wolt.restofinder.data.remote.interceptor

import com.wolt.restofinder.data.remote.exception.NetworkException
import com.wolt.restofinder.data.remote.exception.ServerException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)

            if (!response.isSuccessful) {
                val code = response.code
                val message = response.message
                Timber.w("HTTP error: $code $message for ${request.url}")

                throw when (code) {
                    in 500..599 -> ServerException(code, "Server error: $code $message")
                    else -> ServerException(code, "Request failed: $code $message")
                }
            }

            response
        } catch (e: SocketTimeoutException) {
            Timber.w(e, "Request timeout for ${request.url}")
            throw NetworkException("Request timed out", e)
        } catch (e: UnknownHostException) {
            Timber.w(e, "No internet connection")
            throw NetworkException("No internet connection", e)
        } catch (e: IOException) {
            if (e is ServerException || e is NetworkException) {
                throw e
            }
            Timber.w(e, "Network error for ${request.url}")
            throw NetworkException("Network error: ${e.message}", e)
        }
    }
}
