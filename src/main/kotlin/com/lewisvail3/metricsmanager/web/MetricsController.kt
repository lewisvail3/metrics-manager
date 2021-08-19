package com.lewisvail3.metricsmanager.web

import com.lewisvail3.metricsmanager.dto.SummaryDto
import com.lewisvail3.metricsmanager.exceptions.InvalidValueException
import com.lewisvail3.metricsmanager.exceptions.ServiceException
import com.lewisvail3.metricsmanager.service.MetricService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.NumberFormatException
import java.net.URI

@RestController
@RequestMapping("/v1/metrics")
class MetricsController(
    private val metricService: MetricService
) {

    @PutMapping("/{metricId}")
    fun createMetricIfAbsent(@PathVariable("metricId") metricId: String): ResponseEntity<Void> {
        if (metricService.createIfAbsent(metricId)) {
            return ResponseEntity.created(URI("/v1/metrics/$metricId")).build()
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{metricId}/values")
    fun addMetricValue(@PathVariable("metricId") metricId: String, @RequestBody valueString: String): ResponseEntity<Void> {
        try {
            metricService.addValue(metricId, valueString.toDouble())
        } catch (e: NumberFormatException) {
            throw InvalidValueException("Metric value must be a number")
        }
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{metricId}/stats")
    fun getMetricStats(@PathVariable("metricId") metricId: String): ResponseEntity<SummaryDto> {
        val summary = metricService.getStats(metricId)
        return ResponseEntity.ok(summary)
    }

    @ExceptionHandler(ServiceException::class)
    fun metricExceptionHandler(exception: ServiceException): ResponseEntity<String> {
        return ResponseEntity.status(exception.httpStatus).body(exception.message)
    }
}
