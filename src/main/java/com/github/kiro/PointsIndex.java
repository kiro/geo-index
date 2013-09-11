package com.github.kiro;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Index of points.
 */
public class PointsIndex {
    private final GeoIndex<Set<Point>> points;

    public PointsIndex(Distance size) {
        this.points = new GeoIndex<Set<Point>>(size, new Function<Void, Set<Point>>() {
            @Override
            public Set<Point> apply(java.lang.Void aVoid) {
                return new HashSet<Point>();
            }
        });
    }

    public void add(Point point) {
        points.get(point).add(point);
    }

    public void addAll(List<Point> points) {
        for (Point point : points) {
            add(point);
        }
    }

    public void remove(Point point) {
        points.get(point).remove(point);
    }

    public void update(Point old, Point newPoint) {
        remove(old);
        add(newPoint);
    }

    public Iterable<Point> within(Point topLeft, Point bottomRight) {
        List<Set<Point>> allPoints = points.within(topLeft, bottomRight);

        return Iterables.concat(allPoints);
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
