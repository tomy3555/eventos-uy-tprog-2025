package test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import util.ConfigLoader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Properties;

public class ConfigLoaderTest {

    String oldHome;

    static void resetConfigLoader() {
        try {
            Field loadedF = ConfigLoader.class.getDeclaredField("loaded");
            loadedF.setAccessible(true);
            loadedF.setBoolean(null, false);
            Field propsF = ConfigLoader.class.getDeclaredField("props");
            propsF.setAccessible(true);
            ((Properties) propsF.get(null)).clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Path writeConfig(Path home, String... lines) throws IOException {
        Path dir = home.resolve(".eventosUy");
        Files.createDirectories(dir);
        Path cfg = dir.resolve("config.properties");
        Files.write(cfg, java.util.Arrays.asList(lines));
        return cfg;
    }

    @BeforeEach
    void saveHome() {
        oldHome = System.getProperty("user.home");
    }

    @AfterEach
    void restoreHome() {
        System.setProperty("user.home", oldHome);
    }

    @Test
    void cargaBasicaLeePropiedades() throws Exception {
        Path tmpHome = Files.createTempDirectory("home_cfg1");
        System.setProperty("user.home", tmpHome.toString());
        writeConfig(tmpHome, "host=0.0.0.0", "port=8090", "clave=valor");
        resetConfigLoader();
        assertEquals("0.0.0.0", ConfigLoader.get("host"));
        assertEquals("8090", ConfigLoader.get("port"));
        assertEquals("valor", ConfigLoader.get("clave"));
    }

    @Test
    void cacheSeMantieneAunqueSeBorreElArchivo() throws Exception {
        Path tmpHome = Files.createTempDirectory("home_cfg2");
        System.setProperty("user.home", tmpHome.toString());
        Path cfg = writeConfig(tmpHome, "a=1", "b=2");
        resetConfigLoader();
        assertEquals("1", ConfigLoader.get("a"));
        assertEquals("2", ConfigLoader.get("b"));
        Files.delete(cfg);
        assertEquals("1", ConfigLoader.get("a"));
        assertEquals("2", ConfigLoader.get("b"));
    }

    @Test
    void sinArchivoDevuelveNull() throws Exception {
        Path tmpHome = Files.createTempDirectory("home_cfg3");
        System.setProperty("user.home", tmpHome.toString());
        resetConfigLoader();
        assertNull(ConfigLoader.get("x"));
    }
}