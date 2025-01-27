package edu.escuelaing.arem.ASE.app.service;
/**
 * @author Juan Pablo Daza Pereira
 */
import com.google.gson.Gson;
import edu.escuelaing.arem.ASE.app.model.Movie;
import java.util.*;
/**
 * Servicio para gestionar las operaciones CRUD relacionadas con las películas.
 */
public class MovieService implements RESTService {
    private final Map<Integer, Movie> movieDatabase = new HashMap<>();
    //Instancia de Gson para serializar y deserializar objetos JSON.
    private final Gson gson = new Gson();

    public MovieService() {
        Movie sampleMovie = new Movie("Inception", "Christopher Nolan", "2010");
        movieDatabase.put(sampleMovie.getId(), sampleMovie);
    }
    /**
     * Procesa una solicitud REST basada en el tipo de operación solicitada.
     *
     * @param request Tipo de solicitud (getAll, save, update, delete).
     * @param params  Parámetros necesarios para la operación.
     * @return Respuesta en formato JSON o un mensaje de error si la solicitud no es válida.
     */
    @Override
    public String response(String request, String... params) {
        return switch (request) {
            case "getAll" -> getAllMovies();
            case "save" -> saveMovie(params[0], params[1], params[2]);
            case "update" -> updateMovie(params[0], params[1], params[2], params[3]);
            case "delete" -> deleteMovie(params[0]);
            default -> "Invalid Request";
        };
    }

    private String getAllMovies() {
        System.out.println("Listando todas las películas...");
        return gson.toJson(movieDatabase.values());
    }

    private String saveMovie(String title, String director, String year) {
        System.out.println("Verificando si la película ya existe...");

        boolean exists = movieDatabase.values().stream()
                .anyMatch(movie -> movie.getTitle().equalsIgnoreCase(title));

        if (exists) {
            System.out.println("Error: La película '" + title + "' ya existe");
            return "Error: La película ya existe";
        }

        System.out.println("Guardando película: " + title);
        Movie movie = new Movie(title, director, year);
        movieDatabase.put(movie.getId(), movie);
        return gson.toJson(movie);
    }

    private String updateMovie(String idStr, String title, String director, String year) {
        try {
            int id = Integer.parseInt(idStr);
            System.out.println("Actualizando película con ID: " + id);
            Movie movie = movieDatabase.get(id);
            if (movie != null) {
                movie.update(title, director, year);
                return gson.toJson(movie);
            }
            return "Película no encontrada";
        } catch (NumberFormatException e) {
            return "Formato del ID invalido";
        }
    }

    private String deleteMovie(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            System.out.println("Eliminando película con ID: " + id);
            Movie movie = movieDatabase.remove(id);
            return movie != null ? "Película eliminada" : "Película no encontrada";
        } catch (NumberFormatException e) {
            return "Formato del ID invalido";
        }
    }
}
