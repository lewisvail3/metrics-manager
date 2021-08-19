package com.lewisvail3.metricsmanager.service

import com.lewisvail3.metricsmanager.repo.MetricRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
internal class MetricServiceTest {

    @Mock
    private lateinit var metricRepository: MetricRepository
    private lateinit var metricService: MetricService

    @BeforeEach
    fun setUp() {
        metricService = MetricService(metricRepository)
    }

    @Test
    fun `test add metric value`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(any())).thenReturn(listOf(1.0, 2.5, 100.0))

        metricService.addValue(metricId, 99.9)

        verify(metricRepository).getValues(metricId)
        argumentCaptor<List<Double>>().apply {
            verify(metricRepository).setValues(eq(metricId), capture())
            // The order of the list is important so that we can quickly determine the min, max, and median
            assertThat(firstValue).isEqualTo(listOf(1.0, 2.5, 99.9, 100.0))
        }
    }

    @Test
    fun `test add metric value with new min`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(any())).thenReturn(listOf(2.5, 99.9, 100.0))

        metricService.addValue(metricId, 1.0)

        verify(metricRepository).getValues(metricId)
        argumentCaptor<List<Double>>().apply {
            verify(metricRepository).setValues(eq(metricId), capture())
            assertThat(firstValue).isEqualTo(listOf(1.0, 2.5, 99.9, 100.0))
        }
    }

    @Test
    fun `test add metric value with new max`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(any())).thenReturn(listOf(1.0, 2.5, 99.9))

        metricService.addValue(metricId, 100.0)

        verify(metricRepository).getValues(metricId)
        argumentCaptor<List<Double>>().apply {
            verify(metricRepository).setValues(eq(metricId), capture())
            assertThat(firstValue).isEqualTo(listOf(1.0, 2.5, 99.9, 100.0))
        }
    }

    @Test
    fun `test add metric value with the same max`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(any())).thenReturn(listOf(1.0, 2.5, 99.9, 100.0))

        metricService.addValue(metricId, 100.0)

        verify(metricRepository).getValues(metricId)
        argumentCaptor<List<Double>>().apply {
            verify(metricRepository).setValues(eq(metricId), capture())
            assertThat(firstValue).isEqualTo(listOf(1.0, 2.5, 99.9, 100.0, 100.0))
        }
    }

    @Test
    fun `test get metric summary with odd number of metrics`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(metricId)).thenReturn(listOf(1.0, 2.0, 3.0, 4.0, 5.0))

        val summaryDto = metricService.getStats(metricId)
        assertThat(summaryDto.average).isEqualTo(3.0)
        assertThat(summaryDto.min).isEqualTo(1.0)
        assertThat(summaryDto.max).isEqualTo(5.0)
        assertThat(summaryDto.median).isEqualTo(3.0)
    }

    @Test
    fun `test get metric summary with even number of metrics`() {
        val metricId = "metric1"

        whenever(metricRepository.getValues(metricId)).thenReturn(listOf(2.5, 2.5, 3.0, 4.0, 5.0, 7.0))

        val summaryDto = metricService.getStats(metricId)
        assertThat(summaryDto.average).isEqualTo(4.0)
        assertThat(summaryDto.min).isEqualTo(2.5)
        assertThat(summaryDto.max).isEqualTo(7.0)
        assertThat(summaryDto.median).isEqualTo(3.0)
    }
}
