package com.wolt.restofinder.data.remote.exception

import java.io.IOException

class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)

class ServerException(val code: Int, message: String) : IOException(message)
