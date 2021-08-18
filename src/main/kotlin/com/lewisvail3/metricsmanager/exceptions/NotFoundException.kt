package com.lewisvail3.metricsmanager.exceptions

import org.springframework.http.HttpStatus

class NotFoundException(message: String) : ServiceException(HttpStatus.NOT_FOUND, message)
