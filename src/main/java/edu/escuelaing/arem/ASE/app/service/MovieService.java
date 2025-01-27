package edu.escuelaing.arem.ASE.app.service;

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

    private String saveMovie(String title, String director, String year) {
        Movie movie = new Movie(title, director, year);
        movieDatabase.put(movie.getId(), movie);
        return gson.toJson(movie);
    }

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
