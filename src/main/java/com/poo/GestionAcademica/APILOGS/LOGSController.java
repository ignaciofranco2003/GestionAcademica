package com.poo.GestionAcademica.APILOGS;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class LOGSController {

    static String baseLoginString = "http://poo-dev.unsada.edu.ar:8080/sistema_autogestion/logs/";
    
    public List<Log> findLogs(int studentId) {
        try {
            String baseLoginStringAux = baseLoginString + "student" + studentId;

            URL baseLogin = new URL(baseLoginStringAux);

            HttpURLConnection conexionLogin = (HttpURLConnection) baseLogin.openConnection();
            conexionLogin.setRequestMethod("GET");
            conexionLogin.connect();

            int responseCode = conexionLogin.getResponseCode();

            if (responseCode != 200) {
                List<Log> logsList = new ArrayList<>();
                if (responseCode == 400) {
                    System.out.println("No hay logs para student" + studentId);
                    return logsList;
                } else {
                    System.out.println("No es posible conectar para student " + studentId + ": " + responseCode);
                    return logsList;
                }
            } else {
                StringBuilder informacionJson = new StringBuilder();
                Scanner in = new Scanner(conexionLogin.getInputStream());
                while (in.hasNext()) {
                    informacionJson.append(in.nextLine());
                }
                in.close();

                // Parsear el JSON recibido
                JSONArray responseJson = new JSONArray(informacionJson.toString());

                // Convertir el JSONArray a una lista de objetos Log
                List<Log> logsList = new ArrayList<>();
                for (int i = 0; i < responseJson.length(); i++) {
                    JSONObject logJson = responseJson.getJSONObject(i);
                    Log log = new Log();
                    log.setTimestamp(logJson.getString("timestamp"));
                    log.setCourseId(logJson.getString("courseId"));
                    log.setEvent(logJson.getString("event"));
                    logsList.add(log);

                    System.out.println(logJson.getString("timestamp"));
                    System.out.println(logJson.getString("courseId"));
                    System.out.println(logJson.getString("event"));
                }

                return logsList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            List<Log> logsList = new ArrayList<>();
            return logsList;
        }
    }
}

/*
 * public List<String> findLogs(int studentId) {
 * try {
 * String baseLoginStringAux = baseLoginString + "student" + studentId;
 * 
 * URL baseLogin = new URL(baseLoginStringAux);
 * 
 * HttpURLConnection conexionLogin = (HttpURLConnection)
 * baseLogin.openConnection();
 * conexionLogin.setRequestMethod("GET");
 * conexionLogin.connect();
 * 
 * int responseCode = conexionLogin.getResponseCode();
 * 
 * if (responseCode != 200) {
 * if (responseCode == 400) {
 * System.out.println("No hay logs para student" + studentId);
 * return null;
 * } else {
 * System.out.println("No es posible conectar para student " + studentId + ": "
 * + responseCode);
 * return null;
 * }
 * } else {
 * StringBuilder informacionJson = new StringBuilder();
 * Scanner in = new Scanner(baseLogin.openStream());
 * while (in.hasNext()) {
 * informacionJson.append(in.nextLine());
 * }
 * in.close();
 * 
 * // Parsear el JSON recibido
 * JSONArray responseJson = new JSONArray();
 * 
 * return responseJson;
 * }
 * } catch (Exception e) {
 * e.printStackTrace();
 * return null;
 * }
 * }
 */
