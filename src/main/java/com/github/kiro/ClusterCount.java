package com.github.kiro;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Point.point;

/**
 * Clustering counter.
 */
public class ClusterCount {
    private double latSum = 0;
    private double lonSum = 0;
    private int count = 0;

    public void add(Point p) {
        latSum += p.lat;
        lonSum += p.lon;
        count++;
    }

    public void remove(Point p) {
        latSum -= p.lat;
        lonSum -= p.lon;
        count--;
    }

    public DataPoint<AtomicInteger> dataPoint() {
        return new DataPoint<AtomicInteger>(
            new AtomicInteger(count), point("", latSum / count, lonSum / count)
        );
    }
}
