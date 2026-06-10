package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BookingAppel implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        printTest();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), "UTF-8");

                int idRestau = Integer.parseInt(extractValeur(requestBody, "idRestau"));
                int nbrPersonnes = Integer.parseInt(extractValeur(requestBody, "nbrPersonnes"));
                String date = extractValeur(requestBody, "date");
                String periode = extractValeur(requestBody, "periode");
                String prenom = extractValeur(requestBody, "prenom");
                String nom = extractValeur(requestBody, "nom");
                String telephone = extractValeur(requestBody, "telephone");

                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                ServiceRMI service = (ServiceRMI) registry.lookup("NomServiceMartin");

                String jsonResponse = service.reserverTable(idRestau, date, periode, nbrPersonnes, prenom, nom, telephone);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes("UTF-8").length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes("UTF-8"));
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = "{\"error\": \"Échec de l'enregistrement de la réservation via RMI\"}";
                exchange.sendResponseHeaders(500, errorMsg.length());
                exchange.getResponseBody().write(errorMsg.getBytes());
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    /**
     * extraire une valeur d'une chaîne JSON simple
     * sans utiliser de librairie externe.
     */
    private String extractValeur(String json, String cle) {
        String recherche = "\"" + cle + "\":";
        int debut = json.indexOf(recherche);
        if (debut == -1) return "0";
        debut += recherche.length();
        int fin = json.indexOf(",", debut);
        if (fin == -1) fin = json.indexOf("}", debut);
        return json.substring(debut, fin).replaceAll("[\"\\s}]", "").trim();
    }

    public static void printTest(){
        System.out.println("testsfsgsg");
    }
}