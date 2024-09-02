package com.poo.GestionAcademica.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poo.GestionAcademica.entities.UserStudents;
import com.poo.GestionAcademica.repositories.UserStudentsRepository;

@Service
public class UserStudentsService {

    @Autowired
    private UserStudentsRepository userStudentsRepository;

    public List<UserStudents> findAll() {
        return userStudentsRepository.findAll();
    }

    public UserStudents save(UserStudents userStudents) {
        return userStudentsRepository.save(userStudents);
    }

    public void deleteByStudentID(int studentID) {
        Optional<UserStudents> userStudent = userStudentsRepository.findByStudentID(studentID);
        if (userStudent.isPresent()) {
            userStudentsRepository.delete(userStudent.get());
        } else {
            throw new EntityNotFoundException("Student with ID " + studentID + " not found");
        }
    }

}
