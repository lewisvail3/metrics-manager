package com.lewisvail3.metricsmanager.exceptions

import org.springframework.http.HttpStatus

class InvalidValueException(message: String) : ServiceException(HttpStatus.BAD_REQUEST, message)
