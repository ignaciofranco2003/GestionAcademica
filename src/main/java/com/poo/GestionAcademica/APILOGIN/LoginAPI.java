package com.poo.GestionAcademica.APILOGIN;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginAPI {

    private final RestTemplate restTemplate;

    static String baseLoginString = "http://poo-dev.unsada.edu.ar:4000/sistema_login/login"; // URL de la API

    public LoginAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static boolean loginUser(String username, String password) {
        HttpURLConnection conexionLogin = null;
        OutputStream os = null;
        BufferedReader br = null;

        try {
            URL baseLogin = new URL(baseLoginString);
            System.out.println(baseLogin);

            conexionLogin = (HttpURLConnection) baseLogin.openConnection();
            conexionLogin.setRequestMethod("POST");
            conexionLogin.setRequestProperty("Content-Type", "application/json; utf-8");
            conexionLogin.setRequestProperty("Accept", "application/json");
            conexionLogin.setDoOutput(true);

            // Crear el JSON de solicitud con usuario y contraseña
            String jsonInputString = new JSONObject()
                    .put("username", username)
                    .put("password", password)
                    .toString();

            // Enviar el JSON de solicitud
            os = conexionLogin.getOutputStream();
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

            // Obtener el código de respuesta
            int responseCode = conexionLogin.getResponseCode();

            if (responseCode != 201) {
                throw new RuntimeException("No es posible conectarse: " + responseCode);
            } else {
                StringBuilder informacionJson = new StringBuilder();
                br = new BufferedReader(new InputStreamReader(conexionLogin.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    informacionJson.append(responseLine.trim());
                }

                System.out.println("Response JSON: " + informacionJson.toString());

                // Procesar el JSON de respuesta usando org.json
                JSONObject responseJson = new JSONObject(informacionJson.toString());

                // Muestra de datos por consola (se puede borrar)
                String token = responseJson.getString("token");
                String userId = responseJson.getString("userId");
                // long userId = responseJson.getLong("userId");
                System.out.println("Token: " + token);
                System.out.println("User ID: " + userId);

                return authorizeLogin(responseJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (os != null) {
                    os.close();
                }
                if (br != null) {
                    br.close();
                }
                if (conexionLogin != null) {
                    conexionLogin.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean authorizeLogin(JSONObject responseJson) {
        HttpURLConnection conexionAuthorize = null;
        OutputStream os = null;
        BufferedReader br = null;
        try {
            String baseAuthorizeString = "http://poo-dev.unsada.edu.ar:4000/sistema_login/authorize"; // URL de la API
            URL baseAuthorize = new URL(baseAuthorizeString);
            System.out.println(baseAuthorize);

            conexionAuthorize = (HttpURLConnection) baseAuthorize.openConnection();
            conexionAuthorize.setRequestMethod("POST");
            conexionAuthorize.setRequestProperty("Content-Type", "application/json; utf-8");
            conexionAuthorize.setRequestProperty("Accept", "application/json");
            conexionAuthorize.setDoOutput(true);

            // Crear el JSON de solicitud con token y systemId
            String token = responseJson.getString("token");
            // String systemId = "sistema2"; // SystemId siempre es "2"
            String systemId = "GESTION_ACADEMICA"; // SystemId siempre es "GESTION_ACADEMICA"
            String jsonInputString = new JSONObject()
                    .put("token", token)
                    .put("systemId", systemId)
                    .toString();

            // Enviar el JSON de solicitud
            os = conexionAuthorize.getOutputStream();
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

            // Obtener el código de respuesta
            int responseCode = conexionAuthorize.getResponseCode();

            if (responseCode != 200) {
                if (responseCode == 403) {
                    System.out.println("Usuario no autorizado: " + responseCode);
                    return false;
                } else {
                    throw new RuntimeException("No es posible conectarse: " + responseCode);
                }
            } else {
                StringBuilder informacionJson = new StringBuilder();
                br = new BufferedReader(
                        new InputStreamReader(conexionAuthorize.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    informacionJson.append(responseLine.trim());
                }

                System.out.println("Response JSON: " + informacionJson.toString());

                // Procesar el JSON de respuesta usando org.json
                JSONObject responseJsonAuthorize = new JSONObject(informacionJson.toString());

                boolean authorized = responseJsonAuthorize.getBoolean("authorized");
                System.out.println("Authorized: " + authorized);

                if (authorized) {
                    // Asignar rol de administrador al usuario
                    System.out.println("Usuario autorizado como administrador");
                    return true;
                } else {
                    System.out.println("No autorizado");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (os != null) {
                    os.close();
                }
                if (br != null) {
                    br.close();
                }
                if (conexionAuthorize != null) {
                    conexionAuthorize.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}
