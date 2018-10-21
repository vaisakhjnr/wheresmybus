package com.wheresmybus.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String PREFERENCES = "wheresmybus_shared_prefs";

    private static PreferenceHelper instance;
    private SharedPreferences preferences;

    private PreferenceHelper(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    public void writeString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String readString(String key) {
        return preferences.getString(key, null);
    }

    public void writeInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int readInt(String key) {
        return preferences.getInt(key, 0);
    }

    public void writeBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean readBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
}