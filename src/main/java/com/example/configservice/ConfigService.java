package com.example.configservice;

import java.util.Properties;
import java.io.InputStream;
import java.util.logging.Logger;


public class ConfigService {

    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());

    private static final Properties appProps = new Properties();
    private static final Properties securityProps = new Properties();
    private static final Properties testingProps = new Properties();

    static {
        load(appProps, "configurationfiles/application.properties");
        load(securityProps, "configurationfiles/security.properties");
        load(testingProps, "configurationfiles/testing.properties");
    }

    private static void load(Properties props, String fileName) {
        try (InputStream is = ConfigService.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (is == null) {
                logger.severe("Config file not found: " + fileName);
                throw new RuntimeException("Config file not found: " + fileName);
            }

            props.load(is);
            logger.info("Loaded config file: " + fileName);

        } catch (Exception e) {
            logger.severe("Failed to load config file: " + fileName + " Error: " + e.getMessage());
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }

    public static String getApp(String key) {
        return getRequired(appProps, key, "application");
    }

    public static String getSecurity(String key) {
        return getRequired(securityProps, key, "security");
    }

    public static String getTesting(String key) {
        return getRequired(testingProps, key, "testing");
    }

    private static String getRequired(Properties props, String key, String source) {
        String value = props.getProperty(key);

        if (value == null) {
            logger.severe("Missing key '" + key + "' in " + source + " config");
            throw new RuntimeException("Missing key '" + key + "' in " + source + " config");
        }

        return value;
    }
}