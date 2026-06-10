package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TrafficAppel implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //Gerer le CORS pour permettre au JavaScript de webetu de lire la reponse
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        printTest();
        //Requetes Preflight OPTIONS envoyees par navigateurs
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                //HttpClient pour appeler l API Waze / G-NY externe
                //IUT : configurer le proxy de l iut dans ce httpclient
                /*HttpClient client = HttpClient.newBuilder()j
                        // Remplacer adresse et port fourni dans la doc de l iut
                        .proxy(java.net.ProxySelector.of(new java.net.InetSocketAddress("proxy.iutnc.univ-lorraine.fr", 3128)))
                        .build();*/
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"))                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonResponse = response.body();

                //Renvoyer la reponse JSON au format HTTP 200 (OK)
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();

            } catch (Exception e) {
                String errorMsg = "{\"error\": \"Erreur lors de la récupération des données de traffic\"}";
                exchange.sendResponseHeaders(500, errorMsg.length());
                exchange.getResponseBody().write(errorMsg.getBytes());
                exchange.getResponseBody().close();
            }
        } else {
            // Methode non autorisee (POST sur une route GET)
            exchange.sendResponseHeaders(405, -1);
        }
    }
    public static void printTest(){
        System.out.println("testsfsgsg");
    }
}

