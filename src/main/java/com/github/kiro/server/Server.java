package com.github.kiro.server;

import au.com.bytecode.opencsv.CSVReader;
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
    private final String rootPath;

    public Server(int port, String rootPath, PointsIndex pointsIndex) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.rootPath = rootPath;
        this.pointsIndex = pointsIndex;
    }

    public void start() {
        server.createContext("/", new FileHandler(rootPath));
        server.createContext("/query", new QueryHandler(pointsIndex));
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
        new NsqListener(pointsIndex, "localhost", 4153).listen();
        new Server(8080, "html", pointsIndex).start();
    }
}
