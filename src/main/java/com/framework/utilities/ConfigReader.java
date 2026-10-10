package com.framework.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads global framework configuration from {@code config.properties} on the classpath.
 * Values here are defaults; individual settings (e.g. browser) can still be overridden
 * per-run via testng.xml parameters or -D system properties.
 */
public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                log.warn("{} not found on classpath, falling back to hard-coded defaults.", CONFIG_FILE);
            } else {
                PROPERTIES.load(input);
                log.info("Loaded configuration from {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
        // Utility class — no instances.
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required config key: " + key);
        }
        return value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }
}
