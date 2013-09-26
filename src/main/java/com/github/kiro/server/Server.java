package com.github.kiro.server;

import au.com.bytecode.opencsv.CSVReader;
import com.github.kiro.CountIndex;
import com.github.kiro.Point;
import com.github.kiro.PointsIndex;
import com.sun.net.httpserver.HttpServer;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.List;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Point.point;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Http server serving a geoindex.
 */
public class Server {
    private HttpServer server;
    private PointsIndex pointsIndex;
    private CountIndex countIndex;

    private final String rootPath;

    public Server(int port, String rootPath, PointsIndex pointsIndex, CountIndex countIndex) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.rootPath = rootPath;
        this.pointsIndex = pointsIndex;
        this.countIndex = countIndex;
    }

    public void start() {
        server.createContext("/", new FileHandler(rootPath));
        server.createContext("/points", new PointsHandler(pointsIndex));
        server.createContext("/counts", new CountHandler(countIndex));
        server.start();

        System.out.println("Started listening...");
    }

    private static List<Point> tubeStations() throws Exception {
        List<Point> stations = newArrayList();
        CSVReader reader = new CSVReader(new FileReader("html/tube.csv"));

        for (String [] parts : reader.readAll()) {
            stations.add(point(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
        }

        return stations;
    }

    public static void main(String [] args) throws Exception {
        PointsIndex pointsIndex = new PointsIndex(km(0.5));
        //pointsIndex.addAll(tubeStations());
        //new NsqListener(pointsIndex, "vpcutilities01-global01-test.i.hailocab.com", 4150).listen();

        CountIndex countIndex = new CountIndex(km(2));
        for (Point station : tubeStations()) {
            countIndex.update(station);
        }

        //new NsqListener(pointsIndex, "localhost", 4153).listen();
        new Server(8080, "html", pointsIndex, countIndex).start();
        System.out.println("Server started...");
    }
}
