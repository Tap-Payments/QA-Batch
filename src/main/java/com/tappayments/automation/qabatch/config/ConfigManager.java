package com.tappayments.automation.qabatch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input != null)
                properties.load(input);

        } catch (IOException ex) {
            throw new RuntimeException("Error loading config.properties file", ex);
        }
    }

    public static String getPropertyValue(String key) {
        return properties.getProperty(key);
    }
}
