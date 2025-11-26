package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private static String getConfigPath() {
        String userHome = System.getProperty("user.home");
        String fileSeparator = System.getProperty("file.separator");
        return userHome + fileSeparator + ".eventosUy" + fileSeparator + "config.properties";
    }

    public static void load() {
        if (loaded) return;

        String configPath = getConfigPath();
        File configFile = new File(configPath);

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            loaded = true;
        } catch (IOException e) {
            
        }
    }

    public static String get(String key) {
        load();
        return props.getProperty(key);
    }
}
