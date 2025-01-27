package edu.escuelaing.arem.ASE.app;
/**
 * @author Juan Pablo Daza Pereira
 */
import edu.escuelaing.arem.ASE.app.service.RESTService;
import edu.escuelaing.arem.ASE.app.service.MovieService;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
/**
 * Clase que implementa un servidor web simple para servir archivos estáticos y manejar
 * solicitudes REST para operaciones relacionadas con películas.
 */
public class SimpleWebServer {
    private static final int PORT = 8080;
    public static final String WEB_ROOT = "src/main/resources";
    public static final Map<String, RESTService> services = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = new ServerSocket(PORT);
        addServices();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            threadPool.submit(new ClientHandler(clientSocket));
        }
    }

    private static void addServices() {
        services.put("Movies", new MovieService());
    }
}
/**
 * Clase que maneja las conexiones de los clientes.
 */
class ClientHandler implements Runnable {
    // Socket del cliente.
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

            if (fileRequested.startsWith("/api/movies")) {
                handleAPIRequest(method, fileRequested, out, dataOut);
            } else {
                if (fileRequested.equals("/")) fileRequested = "/index.html";
                handleFileRequest(fileRequested, out, dataOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Maneja solicitudes relacionadas con la API REST de películas.
     *
     * @param method        Método HTTP (GET, POST, PUT, DELETE).
     * @param fileRequested Ruta solicitada.
     * @param out           Escritor para enviar cabeceras HTTP.
     * @param dataOut       Flujo de datos para enviar el cuerpo de la respuesta.
     * @throws IOException Si ocurre un error de E/S.
     */
    private void handleAPIRequest(String method, String fileRequested, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        String query = fileRequested.contains("?") ? fileRequested.split("\\?")[1] : "";
        Map<String, String> params = parseQueryParams(query);

        String response = switch (method) {
            case "GET" -> SimpleWebServer.services.get("Movies").response("getAll", "");
            case "POST" -> SimpleWebServer.services.get("Movies").response("save",
                    params.get("title"), params.get("director"), params.get("year"));
            case "PUT" -> SimpleWebServer.services.get("Movies").response("update",
                    params.get("id"), params.get("title"), params.get("director"), params.get("year"));
            case "DELETE" -> SimpleWebServer.services.get("Movies").response("delete", params.get("id"));
            default -> "Method Not Allowed";
        };

        sendJSONResponse(out, dataOut, response);
    }
    /**
     * Maneja solicitudes de archivos estáticos.
     *
     * @param fileRequested Ruta del archivo solicitado.
     * @param out           Escritor para enviar cabeceras HTTP.
     * @param dataOut       Flujo de datos para enviar el contenido del archivo.
     * @throws IOException Si ocurre un error de E/S.
     */
    private void handleFileRequest(String fileRequested, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        File file = new File(SimpleWebServer.WEB_ROOT, fileRequested);
        if (file.exists()) {
            byte[] fileData = readFileData(file);
            sendResponse(out, dataOut, fileData, getContentType(fileRequested));
        } else {
            sendError(out, 404, "File Not Found");
        }
    }

    //Parseo
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (!query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
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

    private byte[] readFileData(File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fileIn = new FileInputStream(file)) {
            fileIn.read(fileData);
        }
        return fileData;
    }
}