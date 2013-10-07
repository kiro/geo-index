package com.github.kiro;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Distance.km;
import static com.google.common.collect.Lists.newArrayList;

/**
 * An index that clusters the points at different zoom levels.
 */
public class ClusteringIndex {
    private final PointsIndex streetLevel = new PointsIndex(km(0.5));
    private final CountIndex cityLevel = new CountIndex(km(5));
    private final CountIndex worldLevel = new CountIndex(km(500));

    public void update(Point p) {
        streetLevel.update(p);
        cityLevel.update(p);
        worldLevel.update(p);
    }

    public void remove(Point p) {
        streetLevel.remove(p);
        cityLevel.remove(p);
        worldLevel.remove(p);
    }

    public List<Point> kNearest(Point p, int k, Distance maxDistance) {
        return streetLevel.kNearest(p, k, maxDistance);
    }

    public List<DataPoint<AtomicInteger>> within(Point topLeft, Point bottomRight) {
        Distance viewDistance = topLeft.distance(bottomRight);

        if (viewDistance.lessThan(km(20))) {
            List<Point> points = streetLevel.within(topLeft, bottomRight);
            List<DataPoint<AtomicInteger>> result = newArrayList();

            for (Point point : points) {
                result.add(new DataPoint<AtomicInteger>(new AtomicInteger(1), point));
            }

            return result;
        } else if (viewDistance.lessThan(km(1000))) {
            return cityLevel.within(topLeft, bottomRight);
        } else {
            return worldLevel.within(topLeft, bottomRight);
        }
    }

    public int size() {
        return streetLevel.size();
    }
}
