package com.github.kiro.server;

import com.github.kiro.PointsIndex;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.github.kiro.Distance.km;

/**
 * Http server serving a geoindex.
 */
public class Server {
    private HttpServer server;
    private PointsIndex pointsIndex;
    private final String rootPath;

    public Server(int port, String rootPath) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.rootPath = rootPath;
        this.pointsIndex = new PointsIndex(km(0.5));
    }

    public void start() {
        server.createContext("/", new FileHandler(rootPath));
        server.createContext("/query", new QueryHandler(pointsIndex));
        server.start();
    }

    private abstract class ServerHandler implements HttpHandler {
        public abstract String handle(Map<String, String> params);

        @Override
        public final void handle(HttpExchange httpExchange) throws IOException {
            String result = handle(urlParams(httpExchange.getRequestURI()));
            httpExchange.sendResponseHeaders(200, result.length());
            OutputStream responseStream = httpExchange.getResponseBody();
            responseStream.write(result.getBytes());
            responseStream.close();
        }

        private Map<String, String> urlParams(URI uri) {
            Map<String, String> params = new HashMap<String, String>();

            String[] paramValues = uri.getQuery() != null ? uri.getQuery().split("&") : new String [] {};

            for (String paramValue : paramValues) {
                String [] parts = paramValue.split("=", 2);
                params.put(parts[0], parts[1]);
            }

            return params;
        }
    }

    public static void main(String [] args) throws Exception {
        PointsIndex pointsIndex = new PointsIndex(km(0.5));
        new NsqListener(pointsIndex, "vpcutilities01-global01-test.i.hailocab.com", 4150).listen();
    }
}
