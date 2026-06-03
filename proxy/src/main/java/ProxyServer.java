import com.sun.net.httpserver.HttpServer;
import handlers.BookingAppel;
import handlers.RestaurantsAppel;
import handlers.TrafficAppel;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ProxyServer {
    public static void main(String[] args) {
        try {
            //Définir le port (récupéré idéalement depuis un fichier de config)
            int port = 8080; //définition du port
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); //Création du server
            //System.out.println("Proxy HTTP démarré sur le port " + port);

            //Associer les routes aux gestionnaires (Handlers)
            server.createContext("/api/restaurants", new RestaurantsAppel());
            server.createContext("/api/traffic", new TrafficAppel());
            server.createContext("/api/booking", new BookingAppel());

            server.setExecutor(null);
            server.start();
            System.out.println("Proxy HTTP démarré sur le port " + port);
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur proxy : " + e.getMessage());
        }
    }
}

