package com.gprotechnologies.gprodesktop.utils;

import android.content.SharedPreferences;

import com.gprotechnologies.gprodesktop.App;

import static android.content.Context.MODE_PRIVATE;

public class ShapUtils {

    static class SHAP {
        private static SharedPreferences instance = App.context.getSharedPreferences("gprodesktop", MODE_PRIVATE);
    }

    public static void put(String key, String val) {
        SHAP.instance.edit().putString(key, val).apply();
    }

    public static void put(String key, boolean val) {
        SHAP.instance.edit().putBoolean(key, val).apply();
    }

    public static String get(String key, String def) {
        return SHAP.instance.getString(key, def);
    }

    public static boolean get(String key, boolean def) {
        return SHAP.instance.getBoolean(key, def);
    }

}
