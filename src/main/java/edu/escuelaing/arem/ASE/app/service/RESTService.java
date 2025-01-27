package edu.escuelaing.arem.ASE.app.service;
/**
 * @author Juan Pablo Daza Pereira
 */

/**
 * Interfaz que define un servicio REST genérico.
 * Proporciona un método para manejar solicitudes y generar respuestas.
 */
public interface RESTService {
    /**
     * Maneja una solicitud REST y genera una respuesta en formato String.
     *
     * @param request Tipo de solicitud realizada (por ejemplo, "getAll", "save", etc.).
     * @param params  Parámetros adicionales necesarios para procesar la solicitud.
     * @return Respuesta en formato String, que puede incluir datos o mensajes de error.
     */
    String response(String request, String... params);
}
