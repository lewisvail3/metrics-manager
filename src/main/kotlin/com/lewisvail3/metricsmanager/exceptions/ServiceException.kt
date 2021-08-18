package com.lewisvail3.metricsmanager.exceptions

import org.springframework.http.HttpStatus

open class ServiceException(
    val httpStatus: HttpStatus,
    message: String
): RuntimeException(message)
