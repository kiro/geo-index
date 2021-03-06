package com.github.kiro.server;

import com.github.kiro.ClusteringIndex;
import com.github.kiro.DataPoint;
import com.github.kiro.Point;
import com.github.kiro.PointsIndex;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Point.point;

/**
 * Query handler.
 */
public class PointsHandler extends AbstractHandler {
    private final ClusteringIndex pointsIndex;

    public PointsHandler(ClusteringIndex pointsIndex) {
        this.pointsIndex = pointsIndex;
    }

    @Override
    public void handle(URI uri, OutputStream outputStream) throws IOException {
        Map<String, String> params = urlParams(uri);

        double topLeftLat = Double.parseDouble(params.get("topLeftLat"));
        double topLeftLon = Double.parseDouble(params.get("topLeftLon"));
        double bottomRightLat = Double.parseDouble(params.get("bottomRightLat"));
        double bottomRightLon = Double.parseDouble(params.get("bottomRightLon"));

        List<DataPoint<AtomicInteger>> points = pointsIndex.within(
                point("topLeft", topLeftLat, topLeftLon),
                point("bottomRight", bottomRightLat, bottomRightLon)
        );

        outputStream.write(toResultsJson(points));
    }
}
