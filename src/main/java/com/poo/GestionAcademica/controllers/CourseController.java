package com.poo.GestionAcademica.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poo.GestionAcademica.entities.Course;
import com.poo.GestionAcademica.entities.Inscription;
import com.poo.GestionAcademica.entities.Student;
import com.poo.GestionAcademica.services.CourseService;
import com.poo.GestionAcademica.services.InscriptionService;
import com.poo.GestionAcademica.services.StudentService;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping("/courses")
    public ResponseEntity<List<List<Object>>> getAllCourses() {
        try {
            List<Course> courses = courseService.findAll();

            List<List<Object>> coursesData = courses.stream()
                    .map(course -> {
                        List<Object> courseData = new ArrayList<>();
                        courseData.add("courseId: course" + course.getCourseId());
                        courseData.add("courseName: " + course.getCourseName());
                        courseData.add("description: " + course.getDescription());

                        // Obtener los estudiantes inscritos en el curso
                        List<String> students = course.getStudentsInscriptions().stream()
                                .map(inscription -> "student" + inscription.getStudent().getStudentId())
                                .collect(Collectors.toList());
                        courseData.add(Map.of("students", students));

                        return courseData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(coursesData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/courses/{course_id}")
    public ResponseEntity<Object> getCourseById(@PathVariable("course_id") String courseIdStr) {
        int courseId;
        try {
            courseId = extractId(courseIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tcourse not found\n}");
        }
        Course course = courseService.findById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tCourse not found\n}");
        }
        // Construir la lista con los datos del curso y los estudiantes inscriptos
        List<Object> courseData = new ArrayList<>();
        courseData.add("courseId: course" + course.getCourseId());
        courseData.add("courseName: " + course.getCourseName());
        courseData.add("description: " + course.getDescription());

        // Obtener los estudiantes inscritos en el curso
        List<String> students = course.getStudentsInscriptions().stream()
                .map(inscription -> "student" + inscription.getStudent().getStudentId())
                .collect(Collectors.toList());
        courseData.add(Map.of("students", students));

        return ResponseEntity.status(HttpStatus.OK).body(courseData);
    }

    @PostMapping("/courses/{course_id}/enroll/{student_id}")
    public ResponseEntity<String> enrollStudent(@PathVariable("course_id") String courseIdStr,
            @PathVariable("student_id") String studentIdStr) {
        int studentId;
        int courseId;
        try {
            studentId = extractId(studentIdStr);
            courseId = extractId(courseIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tcourse or student not found\n}");
        }

        // Buscar estudiante
        Student student = studentService.findById(studentId);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tStudent not found\n}");
        }

        // Buscar curso
        Course course = courseService.findById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tCourse not found\n}");
        }

        // Buscar la inscripción del estudiante en el curso
        Inscription existingInscription = inscriptionService.findInscriptionByStudentIdAndCourseId(studentId, courseId);
        if (existingInscription != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tEnrollment already exists\n}");
        }

        // Crear la inscripción
        Inscription inscription = new Inscription();
        inscription.setStudent(student);
        inscription.setCourse(course);

        // Guardar la inscripción
        inscriptionService.save(inscription);

        // Agregar la inscripción al estudiante y al curso
        student.getInscriptions().add(inscription);
        course.getStudentsInscriptions().add(inscription);

        // Guardar los cambios en estudiantes y cursos
        studentService.updateStudent(student);
        courseService.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).body("{\n\tEnrollment successful\n}");
    }

    @DeleteMapping("/courses/{course_id}/enroll/{student_id}")
    public ResponseEntity<String> unenrollStudent(@PathVariable("course_id") String courseIdStr,
            @PathVariable("student_id") String studentIdStr) {
        int studentId;
        int courseId;
        try {
            studentId = extractId(studentIdStr);
            courseId = extractId(courseIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tcourse or student not found\n}");
        }
        // Buscar estudiante
        Student student = studentService.findById(studentId);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tStudent not found\n}");
        }

        // Buscar el curso
        Course course = courseService.findById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tCourse not found\n}");
        }

        // Buscar la inscripción del estudiante en el curso
        Inscription inscriptionToRemove = inscriptionService.findInscriptionByStudentIdAndCourseId(studentId, courseId);
        if (inscriptionToRemove == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\n\tEnrollment not found\n}");
        }

        // Remover la inscripción del estudiante en el curso
        course.getStudentsInscriptions().remove(inscriptionToRemove);
        student.getInscriptions().remove(inscriptionToRemove);

        // Eliminar la inscripción de la base de datos
        inscriptionService.deleteById(inscriptionToRemove.getInscriptionId());

        // Guardar los cambios en estudiantes y cursos
        studentService.updateStudent(student);
        courseService.save(course);

        return ResponseEntity.status(HttpStatus.OK).body("{\n\tUnenrollment successful\n}");
    }

    private int extractId(String idString) {
        if (idString.matches("^student\\d+$")) {
            return Integer.parseInt(idString.replace("student", ""));
        } else if (idString.matches("^course\\d+$")) {
            return Integer.parseInt(idString.replace("course", ""));
        } else {
            throw new NumberFormatException("{\n\tcourse not found\n}");
        }
    }

}
