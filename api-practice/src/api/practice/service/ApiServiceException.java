package api.practice.service;

/**
 * Excepción de servicio para encapsular errores de conexión,
 * de respuesta HTTP o de procesamiento de JSON.
 */
public class ApiServiceException extends Exception {

    public ApiServiceException(String message) {
        super(message);
    }

    public ApiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
