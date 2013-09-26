package com.github.kiro;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Files.tubeStations;
import static com.github.kiro.Files.worldCapitals;
import static com.github.kiro.Points.*;
import static com.github.kiro.Point.point;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests GeoIndex.
 */
public class PointsIndexTest {
    @Test
    public void testPoints() {

        List<Point> points = newArrayList(leicester, coventGarden, totenham, picadilly, charring, embankment);

        PointsIndex pointsIndex = new PointsIndex(km(1));
        pointsIndex.addAll(points);

        assertEquals(newHashSet(pointsIndex.kNearest(leicester, 3, km(2))), newHashSet(leicester, coventGarden, charring));
    }

    @Test
    public void testPointsIndex() throws Exception {
        PointsIndex pointsIndex = new PointsIndex(km(0.5));
        pointsIndex.addAll(tubeStations());

        Point kingsCross = point("Kings Cross", 51.529999,-0.124481);
        System.out.println(pointsIndex.kNearest(kingsCross, 5, km(100)));
    }

    @Test
    public void testWithin() throws Exception {
        PointsIndex pointsIndex = new PointsIndex(km(0.5));
        pointsIndex.addAll(tubeStations());

        assertEquals(newHashSet(pointsIndex.within(oxford, embankment)),
                newHashSet(leicester, coventGarden, picadilly, charring));
    }

    @Test
    public void testUpdate() throws Exception {
        PointsIndex pointsIndex = new PointsIndex(km(0.1));
        pointsIndex.addAll(tubeStations());

        Point newLeicester = point(leicester.id, 51.512188,-0.116822);
        pointsIndex.update(newLeicester);

        String points = pointsIndex.kNearest(newLeicester, 20, km(3)).toString();
        int lastIndex = points.indexOf(leicester.id);
        assertTrue(lastIndex >= 0);
        assertEquals(points.indexOf(leicester.id, lastIndex + 1), -1);
    }

    @Test
    public void loadTest() throws Exception {
        List<Point> tubeStations = tubeStations();
        PointsIndex pointsIndex = new PointsIndex(km(0.5));

        int count = 0;
        for (int i = 0; i < 1000; i++) {
            for (Point point : tubeStations) {
                count++;
                pointsIndex.update(point(point.id, point.lat + Math.random(), point.lon + Math.random()));
            }
        }

        System.out.println(count);
    }

    @Test
    public void testWithALotOfPoints() throws Exception {
        List<Point> capitals = worldCapitals();
        List<Point> points = newArrayList();

        PointsIndex pointsIndex = new PointsIndex(km(0.5));

        for (Point capital : capitals) {
            for (int i = 0; i < 500; i++) {
                Point p = point(
                        capital.id + i,
                        capital.lat + Math.random() * 0.1,
                        capital.lon + Math.random() * 0.1
                );
                points.add(p);
                pointsIndex.add(p);
            }
        }

        for (Point p : points) {
            Point next = point(p.id, p.lat + Math.random() * 0.1, p.lon + Math.random() * 0.1);
            pointsIndex.update(next);
        }

        int nearest = 0;
        for (Point p : points) {
           // System.out.println(p);
            List<Point> knearest = pointsIndex.kNearest(p, 10, km(10));
            nearest += knearest.size();
            //System.out.println(p);
            //System.out.println(knearest);
        }
        System.out.println(nearest);
        System.out.println(pointsIndex.size());
    }


}
