package com.example.youplayiguess.utils;

import com.google.gson.Gson;

public class GsonUtil {
    private static Gson gsonInstance = new Gson();

    private GsonUtil() {
    }

    public static Gson getGsonInstance() {
        return gsonInstance;
    }
}
