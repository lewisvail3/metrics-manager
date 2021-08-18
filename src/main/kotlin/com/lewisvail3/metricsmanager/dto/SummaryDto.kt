package com.lewisvail3.metricsmanager.dto

/**
 * Represents the summary data of the values stored for a metric
 */
data class SummaryDto(
    val average: Double,
    val min: Double,
    val max: Double,
    val median: Double
)
