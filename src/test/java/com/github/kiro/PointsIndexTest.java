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

/**
 * Tests GeoIndex.
 */
public class PointsIndexTest {
    @Test
    public void testPoints() {
        Point leicester = point("Leicester Square",51.511291,-0.128242);
        Point coventGarden = point("Covent Garden",51.51276,-0.124507);
        Point totenham = point("Tottenham Court Road",51.516206,-0.13087);
        Point picadilly = point("Piccadilly Circus",51.50986,-0.1337);
        Point charring = point("Charing Cross",51.508359,-0.124803);
        Point embankment = point("Embankment",51.507312,-0.122367);

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
    @Ignore
    public void loadTest() throws Exception {
        List<Point> tubeStations = tubeStations();
        PointsIndex pointsIndex = new PointsIndex(km(0.5));

        int count = 0;
        for (int i = 0; i < 1000; i++) {
            for (Point point : tubeStations) {
                count++;
                pointsIndex.update(point, point(point.id, point.lat + Math.random(), point.lon + Math.random()));
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
