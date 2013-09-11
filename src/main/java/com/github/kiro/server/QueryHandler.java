package com.github.kiro.server;

import com.github.kiro.Point;
import com.github.kiro.PointsIndex;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import static com.github.kiro.Point.point;

/**
 * Query handler.
 */
public class QueryHandler extends AbstractHandler {
    private final PointsIndex pointsIndex;

    public QueryHandler(PointsIndex pointsIndex) {
        this.pointsIndex = pointsIndex;
    }

    @Override
    public void handle(URI uri, OutputStream outputStream) throws IOException {
        Map<String, String> params = urlParams(uri);

        double minLat = Double.parseDouble(params.get("minLat"));
        double maxLat = Double.parseDouble(params.get("maxLat"));
        double minLon = Double.parseDouble(params.get("minLon"));
        double maxLon = Double.parseDouble(params.get("maxLon"));

        Iterable<Point> points = pointsIndex.within(
                point("topLeft", maxLat, minLon),
                point("bottomRight", minLat, maxLon)
        );

        outputStream.write(toResultsJson(points));
    }

}
