package com.github.kiro.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of HttpHandler, containing common functionality.
 */
public abstract class AbstractHandler implements HttpHandler {
    private Gson gson = new Gson();

    /**
     * Writes the response to the outputStream.
     *
     * @param uri that is processed
     * @param outputStream response output stream
     * @throws IOException
     */
    public abstract void handle(URI uri, OutputStream outputStream) throws IOException;

    public byte [] toResultsJson(Object obj) {
        return gson.toJson(obj).getBytes();
    }

    /**
     * Utility method for extracting the get url params.
     *
     * @param uri URI object containing the URL
     * @return name, value map containing all parameters
     */
    public Map<String, String> urlParams(URI uri) {
        Map<String, String> params = new HashMap<String, String>();

        String[] queryValues = uri.getQuery().split("[=&]");
        for (int i = 0; i < queryValues.length; i += 2) {
            params.put(queryValues[i], queryValues[i + 1]);
        }

        return params;
    }

    public final void handle(HttpExchange httpExchange) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.out.println(httpExchange.getRequestURI());
        handle(httpExchange.getRequestURI(), outputStream);
        outputStream.close();

        httpExchange.sendResponseHeaders(200, outputStream.size());
        OutputStream responseStream = httpExchange.getResponseBody();
        responseStream.write(outputStream.toByteArray());
        responseStream.flush();
        responseStream.close();
    }
}