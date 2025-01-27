package edu.escuelaing.arem.ASE.app.service;
/**
 * @author Juan Pablo Daza Pereira
 */
import com.google.gson.Gson;
import edu.escuelaing.arem.ASE.app.model.Movie;
import java.util.*;

public class MovieService implements RESTService {
    private final Map<Integer, Movie> movieDatabase = new HashMap<>();
    private final Gson gson = new Gson();

    public MovieService() {
        Movie sampleMovie = new Movie("Inception", "Christopher Nolan", "2010");
        movieDatabase.put(sampleMovie.getId(), sampleMovie);
    }

    /**
     * Responde a solicitudes REST para realizar operaciones sobre las películas.
     *
     * @param request Tipo de operación solicitada (getAll, save, update, delete).
     * @param params  Parámetros necesarios para realizar la operación.
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
        return gson.toJson(movieDatabase.values());
    }

    /**
     * Guarda una nueva película en la base de datos.
     *
     * @param title    Título de la película.
     * @param director Director de la película.
     * @param year     Año de lanzamiento de la película.
     * @return La película guardada en formato JSON.
     */
    private String saveMovie(String title, String director, String year) {
        Movie movie = new Movie(title, director, year);
        movieDatabase.put(movie.getId(), movie);
        return gson.toJson(movie);
    }

    /**
     * Actualiza una película existente en la base de datos.
     *
     * @param idStr    ID de la película a actualizar.
     * @param title    Nuevo título de la película (puede ser null para no actualizar).
     * @param director Nuevo director de la película (puede ser null para no actualizar).
     * @param year     Nuevo año de lanzamiento de la película (puede ser null para no actualizar).
     * @return La película actualizada en formato JSON o un mensaje de error si no se encontró.
     */
    private String updateMovie(String idStr, String title, String director, String year) {
        try {
            int id = Integer.parseInt(idStr);
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

    /**
     * Elimina una película de la base de datos.
     *
     * @param idStr ID de la película a eliminar.
     * @return Mensaje indicando si la película fue eliminada o si no se encontró.
     */
    private String deleteMovie(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Movie movie = movieDatabase.remove(id);
            return movie != null ? "Película eliminada" : "Película no encontrada";
        } catch (NumberFormatException e) {
            return "Formato del ID invalido";
        }
    }
}
