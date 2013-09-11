package com.github.kiro;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Point.point;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Splits the surface into buckets with certain size.
 */
public class GeoIndex<T> {
    private static final double MIN_LON = -180.0;
    private static final double MIN_LAT = -90.0;
    private static final Distance LAT_DEGREE_LEN = km(111.0);
    private static final Distance LON_DEGREE_LENGTH = km(111.0);

    private final Distance size;
    private Map<Index, T> index;
    private Function<Void, T> factory;

    public GeoIndex(Distance size, Function<Void, T> factory) {
        this.size = size;
        this.index = new ConcurrentHashMap<Index, T>();
        this.factory = factory;
    }

    /**
     * Gets the value in the index corresponding to the point.
     */
    public T get(Point point) {
        Index pointIndex = Index.of(point, size);

        if (index.get(pointIndex) == null) {
            index.put(pointIndex, factory.apply(null));
        }

        return index.get(pointIndex);
    }

    /**
     * Gets a list of values in the index that are within the rectangle specified by
     * topLeft and bottomRight.
     */
    public List<T> within(Point topLeft, Point bottomRight) {
        Index topLeftIndex = Index.of(topLeft, size);
        Index bottomRightIndex = Index.of(bottomRight, size);

        return get(bottomRightIndex.x, topLeftIndex.x, topLeftIndex.y, bottomRightIndex.y);
    }

    public List<T> kNearest(Point point, int k, Distance maxDistance, Function<T, Integer> count) {
        Index idx = Index.of(point, size);

        List<T> result = newArrayList();
        result.add(get(point));
        int totalCount = 0;

        for (int d = 1; size.meters * d < maxDistance.meters; d++) {
            List<T> top = get(idx.x - d, idx.x + d, idx.y + d, idx.y + d);
            List<T> bottom = get(idx.x - d, idx.x + d, idx.y - d, idx.y - d);
            List<T> left = get(idx.x - d, idx.x - d, idx.y - d + 1, idx.y + d - 1);
            List<T> right = get(idx.x + d, idx.x + d, idx.y - d + 1, idx.y + d - 1);

            Iterable<T> all = Iterables.concat(top, bottom, left, right);
            for (T items : all) {
                totalCount += count.apply(items);
            }

            result.addAll(newArrayList(all));

            if (totalCount > k) {
                break;
            }
        }

        return result;
    }

    private List<T> get(int minx, int maxx, int miny, int maxy) {
        List<T> result = new ArrayList<T>();

        for (int x = minx; x <= maxx; x++) {
            for (int y = miny; y <= maxy; y++) {
                Index idx = Index.of(x, y);

                if (index.containsKey(idx)) {
                    result.add(index.get(idx));
                }
            }
        }

        return result;
    }

    private static class Index {
        public final int x, y;

        private Index(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Index of(Point point, Distance size) {
            int x = (int)(( - MIN_LAT + point.lat) * LAT_DEGREE_LEN.meters / size.meters);
            int y = (int)(( - MIN_LON + point.lon) * LON_DEGREE_LENGTH.meters / size.meters);

            return new Index(x, y);
        }

        public static Index of(int x, int y) {
            return new Index(x, y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Index)) {
                return false;
            }
            Index that = (Index)obj;

            return x == that.x && y == that.y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    /**
     * TODO(kiro): cache it
     */
    private static class LonDegreeLength {
        public Distance getAt(double lat) {
            return point("", 0, lat).distance(point("", 1, lat));
        }
    }

    public String toString() {
        return index.toString();
    }
}
