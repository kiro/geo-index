package com.github.kiro;

import java.util.Objects;

import static com.github.kiro.Distance.km;

/**
 * A point.
 */
public class Point {
    private static final double EPS = 0.0001;

    public final double lat;
    public final double lon;
    public final String id;

    private Point(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public static Point point(String id, double lat, double lon) {
        return new Point(id, lat, lon);
    }

    public Distance distance(Point p) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(p.lat - lat);
        double dLng = Math.toRadians(p.lon - lon);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(p.lat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return km(dist);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point)) {
            return false;
        }

        Point that = (Point)obj;

        return id.equals(that.id) && Math.abs(lat - that.lat) < EPS && Math.abs(lon - that.lon) < EPS;
    }

    public String toString() {
        return id + " lat: " + lat + " lon: " + lon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon, id);
    }
}
