package com.github.kiro;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Index of points.
 */
public class PointsIndex {
    private int size = 0;
    private final GeoIndex<Set<Point>> points;
    private final ConcurrentHashMap<String, Point> lastValue = new ConcurrentHashMap<String, Point>();

    public PointsIndex(Distance size) {
        this.points = new GeoIndex<Set<Point>>(size, new Function<Void, Set<Point>>() {
            @Override
            public Set<Point> apply(java.lang.Void aVoid) {
                return new HashSet<Point>();
            }
        });
    }

    public void add(Point point) {
        if (lastValue.containsKey(point.id)) {
            throw new IllegalStateException("Point with id " + point.id + " already exists");
        }
        lastValue.put(point.id, point);
        points.get(point).add(point);
        size++;
    }

    public void addAll(List<Point> points) {
        for (Point point : points) {
            add(point);
        }
    }

    public void remove(Point point) {
        if (lastValue.containsKey(point.id)) {
            Point lastPoint = lastValue.get(point.id);
            points.get(lastPoint).remove(lastPoint);
            lastValue.remove(point.id);
            size--;
        }
    }

    public void update(Point point) {
        remove(point);
        add(point);
    }

    private boolean between(double value, double min, double max) {
        return value > min && value < max;
    }

    public List<Point> within(Point topLeft, Point bottomRight) {
        List<Set<Point>> allPoints = points.within(topLeft, bottomRight);

        List<Point> result = newArrayList();
        for (Point point : Iterables.concat(allPoints)) {
            if (between(point.lat, bottomRight.lat, topLeft.lat) &&
                between(point.lon, topLeft.lon, bottomRight.lon)) {
                result.add(point);
            }
        }

        return result;
    }

    public Iterable<Point> kNearest(Point point, int k, Distance maxDistance) {
        List<Set<Point>> blocks = points.kNearest(point, k, maxDistance, new Function<Set<Point>, Integer>() {
            @Override
            public Integer apply(Set<Point> points) {
                return points.size();
            }
        });

        Iterable<Point> points = Iterables.concat(blocks);
        List<Point> pointsList = newArrayList(points);
        Collections.sort(pointsList, comparator(point));

        return pointsList.subList(0, Math.min(pointsList.size(), k));
    }

    public int size() {
        return size;
    }

    private static Comparator<Point> comparator(final Point point) {
        return new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                return Double.compare(point.distance(p1).meters, point.distance(p2).meters);
            }
        };
    }

    public String toString() {
        return points.toString();
    }
}
