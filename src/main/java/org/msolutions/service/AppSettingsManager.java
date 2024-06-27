package org.msolutions.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppSettingsManager {
    private static final String SETTINGS_FILE = "app.properties";
    private static final String NEXT_ID_KEY = "next.id";
    private static final String SLIDE_DURATION = "slide_duration.value";
    private static final String SHOW_TIME = "show_time.value";
    private static final String SHOW_WEATHER = "show_weather.value";
    private static final String SHOW_VIDEO_WITH_SOUND = "show_video_with_sound.value";
    private static final String SHOW_DEMONSTRATION_IN_WINDOW = "show_demonstration_in_window.value";
    private static final String FILES_LOCATION = "files_location.value";

    private static Properties appSettings;

    static {
        appSettings = new Properties();
        try {
            appSettings.load(new FileInputStream(SETTINGS_FILE));
        } catch (IOException e) {
            // Если файл настроек не найден, создадим его с настройками по умолчанию
            appSettings.setProperty(NEXT_ID_KEY, "0");
            appSettings.setProperty(SLIDE_DURATION, "15");
            appSettings.setProperty(SHOW_TIME, "false");
            appSettings.setProperty(SHOW_WEATHER, "false");
            appSettings.setProperty(SHOW_VIDEO_WITH_SOUND, "true");
            appSettings.setProperty(SHOW_DEMONSTRATION_IN_WINDOW, "false");
            appSettings.setProperty(FILES_LOCATION, System.getProperty("user.dir"));

            saveSettings();
        }
    }

    public static void updateSettings(int slideDuration, boolean showTime,
                                      boolean showWeather, boolean showVideoWithSound,
                                      boolean showDemonstrationInWindow) {
        appSettings.setProperty(SLIDE_DURATION, String.valueOf(slideDuration));
        appSettings.setProperty(SHOW_TIME, String.valueOf(showTime));
        appSettings.setProperty(SHOW_WEATHER, String.valueOf(showWeather));
        appSettings.setProperty(SHOW_VIDEO_WITH_SOUND, String.valueOf(showVideoWithSound));
        appSettings.setProperty(SHOW_DEMONSTRATION_IN_WINDOW, String.valueOf(showDemonstrationInWindow));

        saveSettings();
    }

    public static List<String> getSettings() {
        List<String> propertiesList = new ArrayList<>();
        propertiesList.add(appSettings.getProperty(SLIDE_DURATION));
        propertiesList.add(String.valueOf(appSettings.get(SHOW_TIME)));
        propertiesList.add(String.valueOf(appSettings.get(SHOW_WEATHER)));
        propertiesList.add(String.valueOf(appSettings.get(SHOW_VIDEO_WITH_SOUND)));
        propertiesList.add(String.valueOf(appSettings.get(SHOW_DEMONSTRATION_IN_WINDOW)));
        propertiesList.add(String.valueOf(appSettings.get(FILES_LOCATION)));

        return propertiesList;
    }

    public static int generateId() {
        int Id = Integer.parseInt(appSettings.getProperty(NEXT_ID_KEY));
        int nextId = Id += 1;
        appSettings.setProperty(NEXT_ID_KEY, String.valueOf(nextId));
        saveSettings();
        return Id;
    }

    private static void saveSettings() {
        try {
            appSettings.store(new FileOutputStream(SETTINGS_FILE), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPath() {
        return appSettings.getProperty(FILES_LOCATION);
    }
}
