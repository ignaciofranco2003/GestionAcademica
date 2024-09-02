package com.poo.GestionAcademica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poo.GestionAcademica.entities.Course;
import com.poo.GestionAcademica.entities.Inscription;
import com.poo.GestionAcademica.entities.Student;
import com.poo.GestionAcademica.services.CourseService;
import com.poo.GestionAcademica.services.InscriptionService;
import com.poo.GestionAcademica.services.StudentService;

@Controller
public class CourseCRUDController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping({ "/cursos" })
    public String listarEstudiantes(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "courses";
    }

    @GetMapping("/cursos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("course", new Course());
        return "CreateCourse"; // Retorna al archivo crear_estudiante.html
    }

    @PostMapping("/cursos")
    public String guardarEstudiante(@ModelAttribute("course") Course course) {
        courseService.save(course);
        return "redirect:/cursos"; // Redirige a la lista de cursos
    }

    @GetMapping("/cursos/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") int id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        return "EditCourse"; // Retorna al archivo EditCourse.html
    }

    @PostMapping("/cursos/{id}")
    public String actualizarEstudiante(@PathVariable("id") int id, @ModelAttribute("course") Course course) {
        course.setCourseId(id); // Aseguramos que el ID del estudiante sea el correcto
        courseService.save(course); // Guarda o actualiza al estudiante
        return "redirect:/cursos"; // Redirige a la lista de cursos
    }

    /*
     * @GetMapping("/cursos/eliminar/{id}")
     * public String eliminarEstudiante(@PathVariable("id") int id) {
     * courseService.deleteById(id);
     * return "redirect:/cursos"; // Redirige a la lista de estudiantes
     * }
     */
    @GetMapping("cursos/eliminar/{id}")
    public String deleteCourse(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Curso eliminado exitosamente.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/cursos";
    }

    @GetMapping("/cursos/estudiantes/{id}")
    public String listarEstudiante(@PathVariable("id") int id, Model model) {
        Course course = courseService.findById(id);
        // Agregar estudiantes al modelo
        model.addAttribute("students", course.getCourseStudents());
        model.addAttribute("courseId", course.getCourseId());

        return "SeeStudentsCourses"; // Redirige a la lista de estudiantes
    }

    // Método para eliminar un estudiante de un curso
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cursos/estudiantes/{courseId}/borrar/{studentId}")
    public String bajaEstudianteDeCurso(@PathVariable("courseId") int courseId,
            @PathVariable("studentId") int studentId, Model model) {
        Student estudianteaux = studentService.findById(studentId);
        Course cursoaux = courseService.findById(courseId);

        // Buscar la inscripción del estudiante en el curso
        Inscription inscriptionToRemove = inscriptionService.findInscriptionByStudentIdAndCourseId(studentId, courseId);

        // Verificar si la inscripción existe antes de intentar eliminarla
        if (inscriptionToRemove == null) {
            throw new RuntimeException("Inscription not found");
        }

        // Remover la inscripción del estudiante en el curso
        cursoaux.getStudentsInscriptions().remove(inscriptionToRemove);
        estudianteaux.getInscriptions().remove(inscriptionToRemove);

        // Eliminar la inscripción de la base de datos
        inscriptionService.deleteById(inscriptionToRemove.getInscriptionId());

        // Guardar los cambios en estudiantes y cursos
        studentService.updateStudent(estudianteaux);
        courseService.save(cursoaux);

        model.addAttribute("student", estudianteaux);
        model.addAttribute("courseId", courseId);

        // Redirigir a la página de estudiantes del curso
        return "SeeStudentsCourses";
    }
}