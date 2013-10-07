package com.github.kiro.server;

import au.com.bytecode.opencsv.CSVReader;
import com.github.kiro.ClusteringIndex;
import com.github.kiro.CountIndex;
import com.github.kiro.Point;
import com.github.kiro.PointsIndex;
import com.sun.net.httpserver.HttpServer;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import static com.github.kiro.Distance.km;
import static com.github.kiro.Point.point;
import static com.github.kiro.server.Files.tubeStations;
import static com.github.kiro.server.Files.worldCapitals;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Http server serving a geoindex.
 */
public class Server {
    private HttpServer server;
    private ClusteringIndex index;

    private final String rootPath;

    public Server(int port, String rootPath, ClusteringIndex index) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.rootPath = rootPath;
        this.index = index;
    }

    public void start() {
        server.createContext("/", new FileHandler(rootPath));
        server.createContext("/points", new PointsHandler(index));
        server.createContext("/knearest", new KNearestHandler(index));
        server.start();

        System.out.println("Started listening...");
    }

    public static void listenToNSQ(int ... ports) throws Exception {
        ClusteringIndex index = new ClusteringIndex();

        new Server(8080, "html", index).start();
        new NsqListener(index, "localhost", ports).listen();
    }

    public static void loadTest() throws Exception {
        ClusteringIndex pointsIndex = new ClusteringIndex();

        Random random = new Random();

        System.out.println("Adding points...");
        for (Point capital : worldCapitals()) {
            for (int i = 0; i < 4000; i++) {
                Point p = point(capital.id + i, capital.lat + random.nextGaussian()*0.1, capital.lon + random.nextGaussian()*0.1);
                pointsIndex.update(p);
            }
        }

        System.out.println(pointsIndex.size());

        //new NsqListener(pointsIndex, "localhost", 4153).listen();
        new Server(8080, "html", pointsIndex).start();
        System.out.println("Server started...");
    }

    public static void main(String [] args) throws Exception {
        listenToNSQ(5670, 5671, 5672);
    }
}
