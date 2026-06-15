
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class TrafficAppel implements HttpHandler {
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
        //Gerer le CORS pour permettre au JavaScript de webetu de lire la reponse
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        printTest();
        System.out.println("quoi");
        //Requetes Preflight OPTIONS envoyees par navigateurs
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
                    System.out.println("0");
            return;
        }
        System.out.println("1");
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    System.out.println("1.5");

            try {
                //HttpClient pour appeler l API Waze / G-NY externe
                //IUT : configurer le proxy de l iut dans ce httpclient
                HttpClient client = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress("www-cache.iutnc.univ-lorraine.fr", 3128)))
                    .connectTimeout(Duration.ofSeconds(5))
		    .followRedirects(HttpClient.Redirect.ALWAYS)                    
		    .build();
                                System.out.println("2");

                //HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    //.uri(URI.create("https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json"))
                    .uri(URI.create(config.getProperty("traffic.url")))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
                System.out.println("3");

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonResponse = response.body();
		        System.out.println(response);
                System.out.println("5");


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
                    System.out.println("4");

            // Methode non autorisee (POST sur GET)
            exchange.sendResponseHeaders(405, -1);
        }
    }
    public static void printTest(){
        System.out.println("testsfsgsgqdqdqdqdqd");
    }
}

