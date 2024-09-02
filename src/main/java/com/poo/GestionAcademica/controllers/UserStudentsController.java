package com.poo.GestionAcademica.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poo.GestionAcademica.entities.UserStudents;
import com.poo.GestionAcademica.services.UserStudentsService;

@RestController
@RequestMapping("/users")
public class UserStudentsController {

    @Autowired
    private UserStudentsService userStudentsService;

    @GetMapping()
    public ResponseEntity<List<List<Object>>> getAllusers() {
        List<UserStudents> userStudents = userStudentsService.findAll();

        List<List<Object>> studentsData = userStudents.stream()
                .map(userStudent -> {
                    List<Object> studentData = new ArrayList<>();
                    studentData.add("studentID: " + userStudent.getStudentID());
                    studentData.add("userID: " + userStudent.getUserID());
                    return studentData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(studentsData);
    }

    @GetMapping("{userID}")
    public ResponseEntity<Object> getUserById(@PathVariable("userID") String userIDStr) {
        // Extraer el número del userID
        int userID;
        try {
            userID = extractUserId(userIDStr);
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid user ID format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Buscar el usuario por userID
        Optional<UserStudents> userStudentOpt = userStudentsService.findAll().stream()
                .filter(userStudent -> userStudent.getUserID() == userID)
                .findFirst();

        if (userStudentOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        UserStudents userStudent = userStudentOpt.get();

        // Construir la respuesta con los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("studentID", userStudent.getStudentID());

        return ResponseEntity.status(HttpStatus.OK).body(userData);
    }

    private int extractUserId(String userIDStr) throws NumberFormatException {
        // Regex para extraer el número después del prefijo "user"
        Pattern pattern = Pattern.compile("user(\\d+)");
        Matcher matcher = pattern.matcher(userIDStr);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new NumberFormatException("Invalid user ID format");
        }
    }

}
