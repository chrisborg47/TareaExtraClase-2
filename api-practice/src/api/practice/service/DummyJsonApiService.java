package api.practice.service;

import api.practice.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio responsable de consumir la API DummyJSON usando HttpURLConnection.
 * Incluye métodos para leer todos los usuarios, buscar por ID y extraer correos.
 */
public class DummyJsonApiService {

    private static final String BASE_URL = "https://dummyjson.com/users";
    private static final int TIMEOUT_MS = 10_000;

    private final Gson gson;

    public DummyJsonApiService() {
        this.gson = new Gson();
    }

    public List<User> obtenerTodosLosUsuarios() throws ApiServiceException {
        String response = ejecutarGet(BASE_URL);
        return parsearListadoUsuarios(response);
    }

    public User obtenerUsuarioPorId(int userId) throws ApiServiceException {
        String response = ejecutarGet(BASE_URL + "/" + userId);
        return parsearUsuario(response);
    }

    public List<String> obtenerCorreosDeUsuarios() throws ApiServiceException {
        List<User> usuarios = obtenerTodosLosUsuarios();
        List<String> correos = new ArrayList<>();

        for (User usuario : usuarios) {
            correos.add(usuario.getEmail());
        }

        return correos;
    }

    /**
     * Ejecuta una petición HTTP GET y devuelve el cuerpo de respuesta.
     * Usa BufferedReader e InputStreamReader para lectura de flujo.
     */
    private String ejecutarGet(String endpoint) throws ApiServiceException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");

            int statusCode = connection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                return leerStream(connection.getInputStream());
            }

            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new ApiServiceException("El usuario solicitado no existe en la API.");
            }

            String errorBody = leerStream(connection.getErrorStream());
            throw new ApiServiceException("La API respondió con error HTTP " + statusCode + ". Detalle: " + errorBody);

        } catch (IOException ex) {
            throw new ApiServiceException("No fue posible conectarse a la API. Verifique internet o disponibilidad del servicio.", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Lee un InputStream completo y construye un String con su contenido.
     */
    private String leerStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    /**
     * Parsea la respuesta JSON de /users y convierte el arreglo "users" a lista de objetos User.
     */
    private List<User> parsearListadoUsuarios(String jsonResponse) throws ApiServiceException {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray usersArray = root.getAsJsonArray("users");

            if (usersArray == null || usersArray.isEmpty()) {
                throw new ApiServiceException("La API devolvió una respuesta vacía de usuarios.");
            }

            List<User> usuarios = new ArrayList<>();
            for (int i = 0; i < usersArray.size(); i++) {
                JsonObject userJson = usersArray.get(i).getAsJsonObject();
                usuarios.add(mapearUsuarioDesdeJson(userJson));
            }

            return usuarios;
        } catch (ApiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiServiceException("Error al procesar JSON de listado de usuarios.", ex);
        }
    }

    /**
     * Parsea la respuesta JSON de /users/{id} y la convierte en User.
     */
    private User parsearUsuario(String jsonResponse) throws ApiServiceException {
        try {
            JsonObject userJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return mapearUsuarioDesdeJson(userJson);
        } catch (Exception ex) {
            throw new ApiServiceException("Error al procesar JSON del usuario solicitado.", ex);
        }
    }

    /**
     * Mapea campos JSON a la clase User.
     */
    private User mapearUsuarioDesdeJson(JsonObject userJson) {
        User user = gson.fromJson(userJson, User.class);

        String city = "No disponible";
        if (userJson.has("address") && userJson.get("address").isJsonObject()) {
            JsonObject addressObject = userJson.getAsJsonObject("address");
            if (addressObject.has("city") && !addressObject.get("city").isJsonNull()) {
                city = addressObject.get("city").getAsString();
            }
        }

        user.setCity(city);
        return user;
    }
}
