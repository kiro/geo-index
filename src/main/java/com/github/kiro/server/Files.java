package com.github.kiro.server;

import au.com.bytecode.opencsv.CSVReader;
import com.github.kiro.Point;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import static com.github.kiro.Point.point;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Test files.
 */
public class Files {
    public static List<Point> worldCapitals() throws Exception {
        return read("capitals.txt", '\t', 2, 3, 4);
    }

    public static List<Point> tubeStations() throws Exception {
        return read("tube.csv", ',', 0, 1, 2);
    }

    public static List<Point> read(String file, char separator, int name, int lat, int lon) throws Exception {
        List<Point> points = newArrayList();
        CSVReader reader = new CSVReader(new FileReader(file), separator);

        for (String [] parts : reader.readAll()) {
            System.out.println(Arrays.toString(parts));
            points.add(point(parts[name], Double.parseDouble(parts[lat]), Double.parseDouble(parts[lon])));
        }

        return points;
    }
}
