package com.lewisvail3.metricsmanager.service

import com.lewisvail3.metricsmanager.dto.SummaryDto
import com.lewisvail3.metricsmanager.exceptions.NotFoundException
import com.lewisvail3.metricsmanager.repo.MetricRepository
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class MetricService(
    private val metricRepository: MetricRepository
) {

    /**
     * Returns true if the metric is created and false if it already exists
     */
    fun createIfAbsent(metricId: String): Boolean {
        return metricRepository.createIfAbsent(metricId)
    }

    fun addValue(metricId: String, value: Double) {
        synchronized(metricId) {
            val existingMetrics = metricRepository.getValues(metricId)
            val updatedMetrics = (existingMetrics + value)
            metricRepository.setValues(metricId, updatedMetrics.sorted())
        }
    }

    fun getSummary(metricId: String): SummaryDto {
        val metricValues = metricRepository.getValues(metricId)
        if (metricValues.isEmpty()) {
            throw NotFoundException("No metric values for metric $metricId")
        }
        return SummaryDto(
            average = metricValues.average(),
            min = metricValues.first(),
            max = metricValues.last(),
            median = computeMedian(metricValues)
        )
    }

    private fun computeMedian(metricValues: List<Double>): Double {
        val medianIndex = ceil(metricValues.size / 2.0).toInt() - 1
        return metricValues[medianIndex]
    }
}
