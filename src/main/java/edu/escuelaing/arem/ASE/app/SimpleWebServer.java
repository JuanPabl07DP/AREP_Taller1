package edu.escuelaing.arem.ASE.app;
/**
 * @author Juan Pablo Daza Pereira
 */
import edu.escuelaing.arem.ASE.app.service.RESTService;
import edu.escuelaing.arem.ASE.app.service.MovieService;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
/**
 * Clase principal que implementa un servidor web simple con soporte para solicitudes
 * REST y archivos estáticos.
 */
public class SimpleWebServer {
    private static final int PORT = 8080;
    public static final String WEB_ROOT = "src/main/resources";
    public static final Map<String, RESTService> services = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("===========================================");
        System.out.println("          Servidor iniciado");
        System.out.println("===========================================");
        System.out.println("Escuchando en puerto: " + PORT);
        System.out.println("Directorio raíz: " + WEB_ROOT);
        System.out.println("===========================================");

        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = new ServerSocket(PORT);
        addServices();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("\n>>> Nueva conexión recibida desde: " +
                    clientSocket.getInetAddress().getHostAddress());
            threadPool.submit(new ClientHandler(clientSocket));
        }
    }

    private static void addServices() {
        services.put("Movies", new MovieService());
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            String requestLine = in.readLine();
            if (requestLine == null) return;

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String fileRequested = tokens[1];

            System.out.println(">>> Request Line: " + requestLine);
            System.out.println(">>> Method: " + method);
            System.out.println(">>> Path: " + fileRequested);

            if (fileRequested.startsWith("/api/movies")) {
                System.out.println(">>> API Request");
                System.out.println(">>> Processing " + method + " request");
                handleAPIRequest(method, fileRequested, out, dataOut);
            } else {
                System.out.println(">>> Static File Request");
                System.out.println(">>> Serving file: " + fileRequested);
                if (fileRequested.equals("/")) fileRequested = "/index.html";
                handleFileRequest(fileRequested, out, dataOut);
            }

            System.out.println(">>> Request completed\n");

        } catch (IOException e) {
            System.err.println(">>> Error processing request: " + e.getMessage());
        }
    }
    /**
     * Maneja solicitudes a la API REST.
     *
     * @param method        Método HTTP (GET, POST, PUT, DELETE).
     * @param fileRequested Ruta solicitada.
     * @param out           Flujo de salida para encabezados HTTP.
     * @param dataOut       Flujo de salida para datos HTTP.
     * @throws IOException Si ocurre un error de E/S.
     */
    private void handleAPIRequest(String method, String fileRequested, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        String query = fileRequested.contains("?") ? fileRequested.split("\\?")[1] : "";
        Map<String, String> params = parseQueryParams(query);

        System.out.println("\n>>> Procesando solicitud " + method + " para películas");
        String response = switch (method) {
            case "GET" -> {
                System.out.println(">>> Obteniendo todas las películas...");
                yield SimpleWebServer.services.get("Movies").response("getAll", "");
            }
            case "POST" -> {
                System.out.println(">>> Creando nueva película...");
                System.out.println(">>> Título: " + params.get("title"));
                System.out.println(">>> Director: " + params.get("director"));
                System.out.println(">>> Año: " + params.get("year"));
                String postResponse = SimpleWebServer.services.get("Movies").response("save",
                        params.get("title"), params.get("director"), params.get("year"));
                if (postResponse.startsWith("Error:")) {
                    System.out.println(">>> Error: Película duplicada");
                    sendError(out, 409, "Película duplicada");
                    yield postResponse;
                }
                yield postResponse;
            }
            case "PUT" -> {
                System.out.println(">>> Actualizando película con ID: " + params.get("id"));
                System.out.println(">>> Nuevo título: " + params.get("title"));
                System.out.println(">>> Nuevo director: " + params.get("director"));
                System.out.println(">>> Nuevo año: " + params.get("year"));
                yield SimpleWebServer.services.get("Movies").response("update",
                        params.get("id"), params.get("title"), params.get("director"), params.get("year"));
            }
            case "DELETE" -> {
                System.out.println(">>> Eliminando película con ID: " + params.get("id"));
                yield SimpleWebServer.services.get("Movies").response("delete", params.get("id"));
            }
            default -> {
                System.out.println(">>> Método no permitido: " + method);
                yield "Method Not Allowed";
            }
        };
        System.out.println(">>> Respuesta: " + response);

        if (!method.equals("POST") || !response.startsWith("Error:")) {
            sendJSONResponse(out, dataOut, response);
        }
    }
    /**
     * Maneja solicitudes de archivos estáticos.
     */
    private void handleFileRequest(String fileRequested, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        File file = new File(SimpleWebServer.WEB_ROOT, fileRequested);
        if (file.exists()) {
            byte[] fileData = Files.readAllBytes(file.toPath());
            sendResponse(out, dataOut, fileData, getContentType(fileRequested));
        } else {
            sendError(out, 404, "File Not Found");
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (!query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                }
            }
        }
        return params;
    }

    private void sendJSONResponse(PrintWriter out, BufferedOutputStream dataOut, String response) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + response.getBytes().length);
        out.println();
        out.flush();
        dataOut.write(response.getBytes());
        dataOut.flush();
    }

    private void sendResponse(PrintWriter out, BufferedOutputStream dataOut, byte[] fileData, String contentType) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + fileData.length);
        out.println();
        out.flush();
        dataOut.write(fileData);
        dataOut.flush();
    }

    private void sendError(PrintWriter out, int code, String message) {
        out.println("HTTP/1.1 " + code + " " + message);
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>" + code + " " + message + "</h1></body></html>");
        out.flush();
    }

    private String getContentType(String file) {
        return switch (file.substring(file.lastIndexOf(".") + 1)) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "svg" -> "image/svg+xml";
            default -> "text/plain";
        };
    }
}