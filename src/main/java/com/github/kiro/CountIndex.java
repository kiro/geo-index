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
    private final GeoIndex<ClusterCount> counters;

    public CountIndex(Distance size) {
        counters = new GeoIndex<ClusterCount>(size, new Function<Void, ClusterCount>() {
            @Override
            public ClusterCount apply(Void aVoid) {
                return new ClusterCount();
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

        counters.get(p).add(p);
    }

    public void remove(Point p) {
        counters.get(p).remove(p);
    }

    public List<DataPoint<AtomicInteger>> within(Point topLeft, Point bottomRight) {
        List<DataPoint<AtomicInteger>> result = newArrayList();
        List<ClusterCount> countersWithin = counters.within(topLeft, bottomRight);

        for (ClusterCount clusterCount : countersWithin) {
            result.add(clusterCount.dataPoint());
        }

        return result;
    }
}
