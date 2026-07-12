package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class
                .getClassLoader()
                .getResourceAsStream("model.properties")) {

            if (in == null) {
                throw new IllegalStateException("model.properties non trovato");
            }

            props.load(in);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }
}
