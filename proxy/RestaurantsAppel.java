import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RestaurantsAppel implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Gestion du CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        System.out.println("connexion");
        // Requete Preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
                    System.out.println("connexion raté");

            return;
        }
        //System.out.println("Méthode reçue : [" + exchange.getRequestMethod() + "]");
        //System.out.println("Est GET : " + "GET".equalsIgnoreCase(exchange.getRequestMethod()));
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    System.out.println("request");

            try {
                Registry registry = LocateRegistry.getRegistry("194.214.170.56", 1099);
                ServiceRMI service = (ServiceRMI) registry.lookup("BDDRestaurant");
                String jsonResponse = service.getCoordonnees();
                System.out.println("dans try");

                // Envoi de la réponse HTTP 200 avec le JSON
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes("UTF-8").length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes("UTF-8"));
                os.close();

            } catch (Exception e) {
                System.err.println("[RMI] ERREUR : " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
                String errorMsg = "{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}";
                byte[] errBytes = errorMsg.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errBytes);
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
                    System.out.println("request raté");

        }
    }
    public static void printTest(){
        System.out.println("testsfsgsg");
    }
}
