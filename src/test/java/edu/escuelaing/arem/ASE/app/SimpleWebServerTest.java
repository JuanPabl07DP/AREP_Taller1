package edu.escuelaing.arem.ASE.app;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleWebServerTest {
    private static final int PORT = 8080;

    @BeforeAll
    public static void setUp() throws IOException {
        new Thread(() -> {
            try {
                SimpleWebServer.main(new String[]{});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllMovies() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Crear una película para la prueba
        String title = URLEncoder.encode("Inception", StandardCharsets.UTF_8);
        String director = URLEncoder.encode("Christopher Nolan", StandardCharsets.UTF_8);
        String year = "2010";

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies?title=" + title +
                        "&director=" + director + "&year=" + year))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        // Obtener todas las películas
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println("GetAll Response: " + responseBody);

        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(""));
        assertTrue(responseBody.contains("\"title\":\"Inception\""));
    }

    @Test
    public void testAddAndGetMovie() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String title = URLEncoder.encode("Matrix", StandardCharsets.UTF_8);
        String director = URLEncoder.encode("Wachowski", StandardCharsets.UTF_8);
        String year = "1999";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies?title=" + title +
                        "&director=" + director + "&year=" + year))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Add Response: " + postResponse.body());
        assertEquals(200, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = getResponse.body();
        System.out.println("Get After Add Response: " + responseBody);
        assertTrue(responseBody.contains("\"title\":\"Matrix\""));
        assertTrue(responseBody.contains("\"director\":\"Wachowski\""));
    }

    @Test
    public void testUpdateMovie() throws IOException, InterruptedException, JSONException {
        HttpClient client = HttpClient.newHttpClient();

        // Primero crear una película
        String title = URLEncoder.encode("Test Movie", StandardCharsets.UTF_8);
        String director = URLEncoder.encode("Test Director", StandardCharsets.UTF_8);
        String year = "2000";

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies?title=" + title +
                        "&director=" + director + "&year=" + year))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create Response: " + createResponse.body());

        // Extraer el ID de la película creada
        JSONObject movie = new JSONObject(createResponse.body());
        int movieId = movie.getInt("id");

        // Actualizar la película
        String newTitle = URLEncoder.encode("Updated Movie", StandardCharsets.UTF_8);
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies?id=" + movieId +
                        "&title=" + newTitle))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = updateResponse.body();
        System.out.println("Update Response: " + responseBody);
        assertEquals(200, updateResponse.statusCode());
        assertTrue(responseBody.contains("\"title\":\"Updated Movie\""));
    }

    @Test
    public void testDeleteMovie() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String id = "1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/api/movies?id=" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Delete Response: " + response.body());
        assertEquals(200, response.statusCode());
        assertEquals("Película eliminada", response.body());
    }

    @Test
    public void testErrors() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/nonexistent"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Error Response: " + response.body());
        assertEquals(404, response.statusCode());
        assertEquals("<html><body><h1>404 File Not Found</h1></body></html>", response.body().trim());
    }
}
