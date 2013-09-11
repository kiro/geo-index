package com.github.kiro;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileReader;
import java.util.List;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Point.point;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests GeoIndex.
 */
public class PointsIndexTest {
    private Point leicester = point("Leicester Square",51.511291,-0.128242);
    private Point coventGarden = point("Covent Garden",51.51276,-0.124507);
    private Point totenham = point("Tottenham Court Road",51.516206,-0.13087);
    private Point picadilly = point("Piccadilly Circus",51.50986,-0.1337);
    private Point charring = point("Charing Cross",51.508359,-0.124803);
    private Point embankment = point("Embankment",51.507312,-0.122367);
    private Point oxford = point("Oxford Circus",51.51511,-0.1417);

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
    @Ignore
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

    private List<Point> tubeStations() throws Exception {
        List<Point> stations = newArrayList();
        CSVReader reader = new CSVReader(new FileReader("tube.csv"));

        for (String [] parts : reader.readAll()) {
            stations.add(point(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
        }

        return stations;
    }
}
