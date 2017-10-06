package com.minerstat.settings;

import java.util.prefs.Preferences;
import javafx.collections.ObservableMap;

/**
 * Helper class for working with user's settings.
 */
public class Settings {

    private static volatile Settings instance;

    public static Settings getInstance() {
        Settings localInstance = instance;
        if (localInstance == null) {
            synchronized (Settings.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Settings();
                }
            }
        }
        return localInstance;
    }

    public synchronized void saveProperties(String PrefName, String value) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(com.minerstat.MainApp.class);
            prefs.put(PrefName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveAllProperties(ObservableMap<String, String> list) {
        list.forEach((k, v) -> Settings.getInstance().saveProperties(k, v));
    }

    public synchronized String getProperties(String PrefName) {
        Preferences prefs = Preferences.userNodeForPackage(com.minerstat.MainApp.class);
        return prefs.get(PrefName, "");
    }

}
