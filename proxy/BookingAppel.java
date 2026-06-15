
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class BookingAppel implements HttpHandler {
    //Partie fichier de config
    private static final Properties config = new Properties();

    static {
        try {
            config.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("config.properties introuvable : " + e.getMessage());
        }
    }
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

                //Si fichier de config
                Registry registry = LocateRegistry.getRegistry(
                    config.getProperty("rmi.host"),
                    Integer.parseInt(config.getProperty("rmi.port"))
                );
                ServiceRMI service = (ServiceRMI) registry.lookup(config.getProperty("rmi.service.restaurants"));
                /*Registry registry = LocateRegistry.getRegistry("194.214.170.56", 1099);
                ServiceRMI service = (ServiceRMI) registry.lookup("BDDRestaurant");*/

                String jsonResponse = service.reserverTable(idRestau, date, periode, nbrPersonnes, prenom, nom, telephone);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes("UTF-8").length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes("UTF-8"));
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = "{\"error\": \"" + e.getMessage() + "\"}";
                byte[] errBytes = errorMsg.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errBytes);
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    /**
     * extraire une valeur d'une chaîne JSON simple sans utiliser de librairieexterne
     */
    private String extractValeur(String json, String cle) {
        String recherche = "\"" + cle + "\":";
        int debut = json.indexOf(recherche);
        if (debut == -1) {
            throw new IllegalArgumentException("Champ manquant : " + cle);
        }
        debut += recherche.length();
        int fin = json.indexOf(",", debut);
        if (fin == -1) {
            fin = json.indexOf("}", debut);
        }
        return json.substring(debut, fin).replaceAll("[\"\\s}]", "").trim();
    }

    public static void printTest() {
        System.out.println("testsfsgsg");
    }
}
