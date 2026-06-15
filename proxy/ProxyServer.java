import java.io.FileInputStream;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class ProxyServer {
    //Partie fichier de config
    private static final Properties config = new Properties();

    static {
        try {
            config.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("config.properties introuvable : " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {
            //Définir le port (récupéré idéalement depuis un fichier de config)
            //int port = 8080; //définition du port
            int port = Integer.parseInt(config.getProperty("proxy.port"));
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); //Création du server
            System.out.println("Proxy HTTP démarré sur le port " + port);

            //Associer les routes aux gestionnaires (Handlers)
            server.createContext("/api/restaurants", new RestaurantsAppel());
            server.createContext("/api/traffic", new TrafficAppel());
            server.createContext("/api/booking", new BookingAppel());

            server.setExecutor(null);
            server.start();

        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur proxy : " + e.getMessage());
        }
    }
    public static void printTest(){
        System.out.println("test");
    }
}

