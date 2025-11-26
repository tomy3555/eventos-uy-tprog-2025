package logica.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public final class ConfigSC {
    private static final String DIR = System.getProperty("user.home") + File.separator + ".eventosUy";
    private static final String FILE = DIR + File.separator + "servidorcentral.properties";
    private static final Properties P = new Properties();

    static {
        try {
            Path p = Paths.get(FILE);
            if (Files.exists(p)) try (InputStream in = Files.newInputStream(p)) { P.load(in); }
            else System.out.println("[CFG][SC] No existe " + FILE + " (usando defaults)");
        } catch (Exception e) {
            System.out.println("[CFG][SC][ERR] " + e.getMessage());
        }
    }

    private ConfigSC(){}

    public static String host()     { return P.getProperty("central.http.host", "0.0.0.0"); }
    public static int port()        { return Integer.parseInt(P.getProperty("central.http.port","8090")); }
    public static String epUsuario(){ return P.getProperty("endpoint.usuario","/publicadorUsuario"); }
    public static String epEvento() { return P.getProperty("endpoint.evento","/publicadorEvento"); }
    public static String epEstad()  { return P.getProperty("endpoint.estadisticas","/publicadorEstadisticas"); }

    // (Opcional) JPA overrides
    public static String jpa(String key, String def){ return P.getProperty(key, def); }
}
