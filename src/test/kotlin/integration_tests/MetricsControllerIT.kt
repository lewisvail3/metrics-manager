package integration_tests

import com.lewisvail3.metricsmanager.MetricsManagerApplication
import com.lewisvail3.metricsmanager.repo.MetricRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [MetricsManagerApplication::class])
class MetricsControllerIT(
    private val context: WebApplicationContext
) {

    @Autowired
    private lateinit var metricRepository: MetricRepository
    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @AfterEach
    fun cleanUp() {
        metricRepository.clearAll()
    }

    @Test
    fun `test create, post, and retrieve`() {
        val metricId = "metric_1"

        // Create the metric
        mvc.perform(put("/v1/metrics/$metricId"))
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/v1/metrics/$metricId"))

        // add data
        mvc.perform(post("/v1/metrics/$metricId").content("100"))
            .andExpect(status().isNoContent)
        mvc.perform(post("/v1/metrics/$metricId").content("103.5"))
            .andExpect(status().isNoContent)
        mvc.perform(post("/v1/metrics/$metricId").content("99.5"))
            .andExpect(status().isNoContent)

        // get summary
        mvc.perform(get("/v1/metrics/$metricId/summary").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.average").value(101))
            .andExpect(jsonPath("$.median").value(100))
            .andExpect(jsonPath("$.min").value(99.5))
            .andExpect(jsonPath("$.max").value(103.5))
    }

    @Test
    fun `test create multiple times`() {
        val metricId1 = "metric_1"
        val metricId2 = "metric_2"

        mvc.perform(put("/v1/metrics/$metricId1"))
            .andExpect(status().isCreated)
        mvc.perform(put("/v1/metrics/$metricId2"))
            .andExpect(status().isCreated)
        mvc.perform(put("/v1/metrics/$metricId1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `test add invalid metric value`() {
        val metricId = "metric_1"

        mvc.perform(put("/v1/metrics/$metricId"))

        mvc.perform(post("/v1/metrics/$metricId").content("not a number"))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Metric value must be a number"))
    }

    @Test
    fun `test add value to non-existent metric`() {
        val metricId = "metric_1"

        mvc.perform(post("/v1/metrics/$metricId").content("100"))
            .andExpect(status().isNotFound)
            .andExpect(content().string("Metric $metricId does not exist")) // TODO consider making this JSON
    }

    @Test
    fun `test read summary from non-existent metric`() {
        val metricId = "metric_1"

        mvc.perform(get("/v1/metrics/$metricId/summary"))
            .andExpect(status().isNotFound)
            .andExpect(content().string("Metric $metricId does not exist"))
    }

    @Test
    fun `test read summary from metric without data`() {
        val metricId = "metric_1"

        mvc.perform(put("/v1/metrics/$metricId"))

        mvc.perform(get("/v1/metrics/$metricId/summary"))
            .andExpect(status().isNotFound)
            .andExpect(content().string("No metric values for metric $metricId"))
    }
}
