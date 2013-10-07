package com.github.kiro;

/**
 * Distance.
 */
public class Distance {
    public final double meters;

    private Distance(double meters) {
        this.meters = meters;
    }

    public boolean lessThan(Distance distance) {
        return this.meters < distance.meters;
    }

    public static Distance meters(double meters) {
        return new Distance(meters);
    }

    public static Distance km(double km) {
        return new Distance(km * 1000);
    }

    @Override
    public String toString() {
        return Double.toString(meters / 1000);
    }
}
