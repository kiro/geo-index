package com.github.kiro;

import com.google.gson.Gson;

/**
 * Point with associated data.
 */
public class DataPoint<T> {
    public final T data;
    public final Point point;

    public DataPoint(T data, Point point) {
        this.data = data;
        this.point = point;
    }

    public static <T> DataPoint<T> dataPoint(T data, Point point) {
        return new DataPoint<T>(data, point);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataPoint)) {
            return false;
        }

        DataPoint that = (DataPoint)obj;
        return point.equals(that.point) && data.equals(that.data);
    }

    @Override
    public String toString() {
        return JsonToString.apply(this);
    }
}
