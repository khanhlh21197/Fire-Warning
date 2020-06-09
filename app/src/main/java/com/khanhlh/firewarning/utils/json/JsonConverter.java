package com.khanhlh.firewarning.utils.json;

import com.google.gson.Gson;

public class JsonConverter {
    public static String objectToJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static <T> Object jsonToObject(String json, Class<T> tClass) {
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }
}
