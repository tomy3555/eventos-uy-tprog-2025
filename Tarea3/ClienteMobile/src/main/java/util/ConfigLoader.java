package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static Properties props = new Properties();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) return;

        // Usar la ruta para Linux explícitamente
        String configPath = System.getProperty("user.home") + "/.eventosUy/config.properties";

        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
            loaded = true;
            System.out.println("Configuración cargada desde: " + configPath);
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo de configuración en: " + configPath);
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        load();
        return props.getProperty(key);
    }
}
