package com.github.kiro.server;

import com.github.kiro.ClusteringIndex;
import com.github.kiro.DataPoint;
import com.github.kiro.Distance;
import com.github.kiro.Point;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Point.point;

/**
 * k-nearest handler.
 */
public class KNearestHandler extends AbstractHandler {
    private final ClusteringIndex index;

    public KNearestHandler(ClusteringIndex index) {
        this.index = index;
    }

    @Override
    public void handle(URI uri, OutputStream outputStream) throws IOException {
        Map<String, String> params = urlParams(uri);

        Distance maxDistance = km(5);
        int k = Integer.parseInt(params.get("k"));
        double lat = Double.parseDouble(params.get("lat"));
        double lon = Double.parseDouble(params.get("lon"));

        List<Point> nearest = index.kNearest(point("k-nearest-query", lat, lon), k, maxDistance);

        outputStream.write(toResultsJson(nearest));
    }
}
