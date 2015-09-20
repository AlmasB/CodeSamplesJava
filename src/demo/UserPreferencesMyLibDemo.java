package com.almasb.java.framework.demo;

import com.almasb.java.util.UserPreferences;

public class UserPreferencesMyLibDemo {

    public static final int APP_W;

    static {
        // on first try reads default
        UserPreferences userPrefs = UserPreferences.getInstance();

        APP_W = userPrefs.get("APP_W", 500);

        userPrefs.put("APP_W", 800);
        userPrefs.save();
        // after saving next time it will read user prefs
    }

    public static void main(String[] args) throws Exception {
        System.out.println(APP_W);
    }
}
