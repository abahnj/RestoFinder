package com.wolt.restofinder.data.remote.interceptor

import com.wolt.restofinder.data.remote.exception.NetworkException
import com.wolt.restofinder.data.remote.exception.ServerException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptorTest {
    private lateinit var interceptor: ErrorInterceptor
    private lateinit var mockRequest: Request

    @Before
    fun setup() {
        interceptor = ErrorInterceptor()
        mockRequest =
            Request.Builder()
                .url("https://api.example.com/test")
                .build()
    }

    @Test
    fun `successful response passes through unchanged`() {
        val successResponse = createMockResponse(200, "OK", "{\"success\":true}")
        val chain = createMockChain(successResponse)

        val result = interceptor.intercept(chain)

        assertEquals(200, result.code)
        assertEquals("OK", result.message)
    }

    @Test
    fun `500 server error throws ServerException`() {
        val errorResponse = createMockResponse(500, "Internal Server Error", "")
        val chain = createMockChain(errorResponse)

        val exception =
            assertThrows(ServerException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(500, exception.code)
        assertEquals("Server error: 500 Internal Server Error", exception.message)
    }

    @Test
    fun `503 service unavailable throws ServerException`() {
        val errorResponse = createMockResponse(503, "Service Unavailable", "")
        val chain = createMockChain(errorResponse)

        val exception =
            assertThrows(ServerException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(503, exception.code)
        assertEquals("Server error: 503 Service Unavailable", exception.message)
    }

    @Test
    fun `400 bad request throws ServerException`() {
        val errorResponse = createMockResponse(400, "Bad Request", "")
        val chain = createMockChain(errorResponse)

        val exception =
            assertThrows(ServerException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(400, exception.code)
        assertEquals("Request failed: 400 Bad Request", exception.message)
    }

    @Test
    fun `404 not found throws ServerException`() {
        val errorResponse = createMockResponse(404, "Not Found", "")
        val chain = createMockChain(errorResponse)

        val exception =
            assertThrows(ServerException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(404, exception.code)
        assertEquals("Request failed: 404 Not Found", exception.message)
    }

    @Test
    fun `SocketTimeoutException wrapped in NetworkException`() {
        val chain =
            object : Interceptor.Chain {
                override fun request() = mockRequest

                override fun proceed(request: Request): Response {
                    throw SocketTimeoutException("Connection timed out")
                }

                override fun connection() = null

                override fun call() = throw NotImplementedError()

                override fun connectTimeoutMillis() = 0

                override fun withConnectTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun readTimeoutMillis() = 0

                override fun withReadTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun writeTimeoutMillis() = 0

                override fun withWriteTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this
            }

        val exception =
            assertThrows(NetworkException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals("Request timed out", exception.message)
    }

    @Test
    fun `UnknownHostException wrapped in NetworkException`() {
        val chain =
            object : Interceptor.Chain {
                override fun request() = mockRequest

                override fun proceed(request: Request): Response {
                    throw UnknownHostException("Unable to resolve host")
                }

                override fun connection() = null

                override fun call() = throw NotImplementedError()

                override fun connectTimeoutMillis() = 0

                override fun withConnectTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun readTimeoutMillis() = 0

                override fun withReadTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun writeTimeoutMillis() = 0

                override fun withWriteTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this
            }

        val exception =
            assertThrows(NetworkException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals("No internet connection", exception.message)
    }

    @Test
    fun `generic IOException wrapped in NetworkException`() {
        val chain =
            object : Interceptor.Chain {
                override fun request() = mockRequest

                override fun proceed(request: Request): Response {
                    throw IOException("Network failure")
                }

                override fun connection() = null

                override fun call() = throw NotImplementedError()

                override fun connectTimeoutMillis() = 0

                override fun withConnectTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun readTimeoutMillis() = 0

                override fun withReadTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun writeTimeoutMillis() = 0

                override fun withWriteTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this
            }

        val exception =
            assertThrows(NetworkException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals("Network error: Network failure", exception.message)
    }

    @Test
    fun `ServerException is re-thrown without wrapping`() {
        val originalException = ServerException(500, "Original error")
        val chain =
            object : Interceptor.Chain {
                override fun request() = mockRequest

                override fun proceed(request: Request): Response {
                    throw originalException
                }

                override fun connection() = null

                override fun call() = throw NotImplementedError()

                override fun connectTimeoutMillis() = 0

                override fun withConnectTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun readTimeoutMillis() = 0

                override fun withReadTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun writeTimeoutMillis() = 0

                override fun withWriteTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this
            }

        val exception =
            assertThrows(ServerException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(originalException, exception)
        assertEquals("Original error", exception.message)
    }

    @Test
    fun `NetworkException is re-thrown without wrapping`() {
        val originalException = NetworkException("Original network error")
        val chain =
            object : Interceptor.Chain {
                override fun request() = mockRequest

                override fun proceed(request: Request): Response {
                    throw originalException
                }

                override fun connection() = null

                override fun call() = throw NotImplementedError()

                override fun connectTimeoutMillis() = 0

                override fun withConnectTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun readTimeoutMillis() = 0

                override fun withReadTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this

                override fun writeTimeoutMillis() = 0

                override fun withWriteTimeout(
                    timeout: Int,
                    unit: java.util.concurrent.TimeUnit,
                ) = this
            }

        val exception =
            assertThrows(NetworkException::class.java) {
                interceptor.intercept(chain)
            }

        assertEquals(originalException, exception)
        assertEquals("Original network error", exception.message)
    }

    private fun createMockResponse(
        code: Int,
        message: String,
        body: String,
    ): Response {
        return Response.Builder()
            .request(mockRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun createMockChain(response: Response): Interceptor.Chain {
        return object : Interceptor.Chain {
            override fun request() = mockRequest

            override fun proceed(request: Request) = response

            override fun connection() = null

            override fun call() = throw NotImplementedError()

            override fun connectTimeoutMillis() = 0

            override fun withConnectTimeout(
                timeout: Int,
                unit: java.util.concurrent.TimeUnit,
            ) = this

            override fun readTimeoutMillis() = 0

            override fun withReadTimeout(
                timeout: Int,
                unit: java.util.concurrent.TimeUnit,
            ) = this

            override fun writeTimeoutMillis() = 0

            override fun withWriteTimeout(
                timeout: Int,
                unit: java.util.concurrent.TimeUnit,
            ) = this
        }
    }
}
