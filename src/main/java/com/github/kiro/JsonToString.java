package com.github.kiro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Converts json to string.
 */
public class JsonToString {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String apply(Object obj) {
        return gson.toJson(obj);
    }
}
