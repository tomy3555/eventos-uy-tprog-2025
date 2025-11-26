package web;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public final class ClienteConfig {
    private static final String DIR = System.getProperty("user.home") + File.separator + ".eventosUy";
    private static final String FILE = DIR + File.separator + "cliente.properties";
    private static final Properties P = new Properties();

    static {
        try {
            Path p = Paths.get(FILE);
            if (Files.exists(p)) try (InputStream in = Files.newInputStream(p)) { P.load(in); }
            else System.out.println("[CFG][CLI] No existe " + FILE + " (usando defaults localhost)");
        } catch (Exception e) {
            System.out.println("[CFG][CLI][ERR] " + e.getMessage());
        }
    }
    private ClienteConfig(){}

    public static String svcUsuario()     { return P.getProperty("svc.usuario",     "http://localhost:8090/publicadorUsuario"); }
    public static String svcEvento()      { return P.getProperty("svc.evento",      "http://localhost:8090/publicadorEvento"); }
    public static String svcEstadisticas(){ return P.getProperty("svc.estadisticas","http://localhost:8090/publicadorEstadisticas"); }

    public static int connectTimeoutMs(){ return Integer.parseInt(P.getProperty("http.connect.timeout","3000")); }
    public static int requestTimeoutMs(){ return Integer.parseInt(P.getProperty("http.request.timeout","3000")); }
}
