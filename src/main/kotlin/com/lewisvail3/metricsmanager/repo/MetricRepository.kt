package com.lewisvail3.metricsmanager.repo

import com.lewisvail3.metricsmanager.exceptions.NotFoundException
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * In-memory repository to store metrics and their values
 */
@Repository
class MetricRepository {
    private val metrics: ConcurrentMap<String, List<Double>> = ConcurrentHashMap()

    /**
     * Returns true if the metric was created. If the record already existed then it returns false
     */
    fun createIfAbsent(metricId: String): Boolean {
        return metrics.putIfAbsent(metricId, emptyList()) == null
    }

    fun getValues(metricId: String): List<Double> {
        return metrics[metricId] ?: throw NotFoundException("Metric $metricId does not exist")
    }

    fun setValues(metricId: String, values: List<Double>) {
        metrics[metricId] = values
    }

    /**
     * Removes all metrics - currently only used to clean up data in tests
     */
    fun clearAll() {
        metrics.clear()
    }
}
