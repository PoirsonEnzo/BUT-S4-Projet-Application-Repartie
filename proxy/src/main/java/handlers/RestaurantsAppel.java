package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RestaurantsAppel implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Gestion du CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        printTest();
        // Requete Preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                ServiceRMI service = (ServiceRMI) registry.lookup("BDDRestaurant");
                String jsonResponse = service.getCoordonnees();

                // Envoi de la réponse HTTP 200 avec le JSON
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes("UTF-8").length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes("UTF-8"));
                os.close();

            } catch (Exception e) {
                String errorMsg = "{\"error\": \"Erreur lors de la communication avec le serveur RMI\"}";
                exchange.sendResponseHeaders(500, errorMsg.length());
                exchange.getResponseBody().write(errorMsg.getBytes());
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
    public static void printTest(){
        System.out.println("testsfsgsg");
    }
}