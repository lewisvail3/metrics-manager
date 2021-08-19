# Metrics Manager

Simple in-memory metrics management service which allows clients store decimal values for a given
metric and retrive summary statistics for the values stored per metric. The summary statistics
include the average (arithmetic mean), median, minimum value, and maximum value.

## Using the Service

The following instructions outline how a client would interact with the Metric Manager

### Creating a New Metric

To create a new metric, issue a PUT request to `/v1/metrics/{metricId}` where `{metricId}` is a
unique string identifying the metric to be created. This request creates the metric if it doesn't
exist, returning a 201, or returns a 204 if the metric was previously created. Because metrics are
stored in a hash map, this request executes in constant time (space complexity is linear). Below is
a curl which demonstrates the create metric request:

```shell
curl -X PUT localhost:8080/v1/metrics/high_temperature
```

### Adding Values to a Metric

New values can be added to a metric by issuing a POST request to `/v1/metrics/{metricId}/values` (
where `{metricId}` is the same unique identifier used when creating the metric) with the value being
added in the request body. Values are stored in an ordered list and order is maintained by adding
new values into the appropriate place in the list while it is copied so that the time and space
complexity of this request is linear. New values result in copying all values into a new list rather
than mutating the existing one so that reads can happen concurrently with writes. Below are a set of
curls which demonstrates to add values to a metric:

```shell
# record a weeks worth of temperatures
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 93.2
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 93.1
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 91.1
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 91.1
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 91.3
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 90.4
curl -X POST localhost:8080/v1/metrics/high_temperature/values -H 'Content-Type: text/plain' -d 90.9
```

### Fetching Summary Statistics for a Metric

Once values have been added to a metric, summary statistics for the metric can be fetched by issuing
a GET request to `/v1/metrics/{metricId}/stats` where `{metricId}` is the identifier used when
creating the metric. This request executes in linear time (as all values must be summed to compute
the average) and with constant space complexity. Below is a curl which demonstrates the add metric
value request:

```shell
curl localhost:8080/v1/metrics/high_temperature/stats
```

The following stats response can be expected for the example metric values above:

```json
{
  "average": 91.58571428571429,
  "median": 91.1,
  "min": 90.4,
  "max": 93.2
}
```

## Building the Service

The following command will build a runnable JAR which can be used to launch the service:

```shell
./mvnw clean package
```

The above command will also run all tests before building the JAR but that step can be omitted by
passing the `-DskipTests` argument.
