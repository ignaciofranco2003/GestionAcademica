package com.poo.GestionAcademica.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.poo.GestionAcademica.entities.Student;
import com.poo.GestionAcademica.services.StudentService;

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<List<List<Object>>> getAllStudents() {
        List<Student> students = studentService.findAll();

        List<List<Object>> studentsData = students.stream()
                .map(student -> {
                    List<Object> studentData = new ArrayList<>();
                    studentData.add("studentId: student" + student.getStudentId());
                    studentData.add("userId: " + student.getUserId());
                    studentData.add("firstName: " + student.getFirstName());
                    studentData.add("lastName: " + student.getLastName());
                    List<String> courses = student.getInscriptions().stream()
                            .map(inscription -> "course" + inscription.getCourse().getCourseId())
                            .collect(Collectors.toList());
                    studentData.add(Map.of("courses", courses));
                    return studentData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(studentsData);
    }

    @GetMapping("/students/{student_id}")
    public ResponseEntity<Object> getStudentById(@PathVariable("student_id") String studentIdStr) {
        int studentId;
        try {
            studentId = extractId(studentIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        Student student = studentService.findById(studentId);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tStudent not found\n}");
        }

        // Construir la lista con los datos del estudiante y los cursos inscriptos
        List<Object> studentData = new ArrayList<>();
        studentData.add("studentId: student" + student.getStudentId());
        studentData.add("userId: " + student.getUserId());
        studentData.add("firstName: " + student.getFirstName());
        studentData.add("lastName: " + student.getLastName());

        List<String> courses = student.getInscriptions().stream()
                .map(inscription -> "course" + inscription.getCourse().getCourseId())
                .collect(Collectors.toList());
        studentData.add(Map.of("courses", courses));

        return ResponseEntity.status(HttpStatus.OK).body(studentData);
    }

    private int extractId(String idString) {
        if (idString.matches("^student\\d+$")) {
            return Integer.parseInt(idString.replace("student", ""));
        } else if (idString.matches("^course\\d+$")) {
            return Integer.parseInt(idString.replace("course", ""));
        } else {
            throw new NumberFormatException("{\n\tStudent not found\n}");
        }
    }
}
