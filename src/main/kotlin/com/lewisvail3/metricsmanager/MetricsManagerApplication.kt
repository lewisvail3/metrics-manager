package com.lewisvail3.metricsmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetricsManagerApplication

fun main(args: Array<String>) {
	runApplication<MetricsManagerApplication>(*args)
}
