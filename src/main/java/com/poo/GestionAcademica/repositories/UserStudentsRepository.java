package com.poo.GestionAcademica.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.GestionAcademica.entities.UserStudents;

@Repository
public interface UserStudentsRepository extends JpaRepository<UserStudents, Integer> {
    Optional<UserStudents> findByStudentID(int studentID);
}
