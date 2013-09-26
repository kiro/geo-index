package com.github.kiro;

import com.google.common.base.Function;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Distance.km;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Keeps a counter for each square.
 */
public class CountIndex {
    private final Map<String, Point> lastPosition;
    private final GeoIndex<AtomicInteger> counters;

    public CountIndex(Distance size) {
        counters = new GeoIndex<AtomicInteger>(size, new Function<Void, AtomicInteger>() {
            @Override
            public AtomicInteger apply(Void aVoid) {
                return new AtomicInteger(0);
            }
        });

        lastPosition = new ConcurrentHashMap<String, Point>();
    }

    public void update(Point p) {
        Point previous;
        if ( (previous = lastPosition.get(p.id)) != null) {
            remove(previous);
        }
        lastPosition.put(p.id, p);

        counters.get(p).incrementAndGet();
    }

    public void remove(Point p) {
        counters.get(p).decrementAndGet();
    }

    public List<DataPoint<AtomicInteger>> within(Point topLeft, Point bottomRight) {
        return counters.dataPointsWithin(topLeft, bottomRight);
    }
}
