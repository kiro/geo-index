package com.github.kiro;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.kiro.Distance.km;
import static com.github.kiro.server.Files.tubeStations;
import static org.junit.Assert.assertEquals;

/**
 * Tests CountIndex.
 */
public class CountIndexTest {
    @Test
    public void testCountIndex() throws Exception {
        CountIndex countIndex = new CountIndex(km(1));

        List<Point> stations = tubeStations();

        for (Point station : stations) {
            countIndex.update(station);
        }

        System.out.println(countIndex.within(Points.oxford, Points.leicester));
    }

    @Test
    public void testUpdate() {
        CountIndex countIndex = new CountIndex(km(50));
        String id = "test";

        countIndex.update(Points.oxford.withId(id));
        countIndex.update(Points.charring.withId(id));
        countIndex.update(Points.embankment.withId(id));
        List<DataPoint<AtomicInteger>> squares = countIndex.within(Points.oxford, Points.leicester);
        assertEquals(squares.size(), 1);
        assertEquals(squares.get(0).data.get(), 1);
    }
}
