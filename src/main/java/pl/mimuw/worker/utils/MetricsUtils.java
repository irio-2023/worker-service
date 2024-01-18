package pl.mimuw.worker.utils;

import com.google.api.Metric;
import com.google.api.MonitoredResource;
import com.google.monitoring.v3.Point;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.monitoring.v3.TypedValue;
import com.google.protobuf.util.Timestamps;

import java.util.List;
import java.util.Map;

public class MetricsUtils {

    public static List<TimeSeries> createListOfTimeSeries(final String projectId, final String metricsName, final Integer pointValue) {
        final var metric = Metric.newBuilder()
                .setType(metricsName)
                .putAllLabels(Map.of())
                .build();
        final var resource = MonitoredResource.newBuilder()
                .setType("global")
                .putAllLabels(Map.of("project_id", projectId))
                .build();
        final var timeSeries = TimeSeries.newBuilder()
                .setMetric(metric)
                .setResource(resource)
                .addAllPoints(createListOfIntegerPoints(pointValue))
                .build();
        return List.of(timeSeries);
    }

    public static List<Point> createListOfIntegerPoints(final Integer pointValue) {
        final var interval = TimeInterval.newBuilder()
                .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();
        final var value = TypedValue.newBuilder()
                .setInt64Value(pointValue)
                .build();
        final var point = Point.newBuilder()
                .setInterval(interval)
                .setValue(value)
                .build();
        return List.of(point);
    }
}
