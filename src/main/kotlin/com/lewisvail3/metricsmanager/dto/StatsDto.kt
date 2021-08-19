package com.lewisvail3.metricsmanager.dto

/**
 * Represents the summary statistics of the values stored for a metric
 */
data class StatsDto(
    val average: Double,
    val median: Double,
    val min: Double,
    val max: Double,
)
