package com.antelopesystem.authframework.base.filter

import org.springframework.http.HttpStatus

class RequestFailedException : RuntimeException {
    val statusCode: Int

    @JvmOverloads
    constructor(statusCode: Int = HttpStatus.FORBIDDEN.value()) : super("Request Failed") {
        this.statusCode = statusCode
    }

    @JvmOverloads
    constructor(message: String?, statusCode: Int = HttpStatus.FORBIDDEN.value()) : super(message) {
        this.statusCode = statusCode
    }

}