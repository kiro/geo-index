package com.github.kiro.server;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Handler that serves the content of the files in the rootPath.
 */
public class FileHandler extends AbstractHandler {
    private final String rootPath;

    public FileHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    public void handle(URI uri, OutputStream outputStream) throws IOException {
        System.out.println(uri);
        File file = new File(rootPath + uri.getPath());
        Files.copy(file, outputStream);
    }
}