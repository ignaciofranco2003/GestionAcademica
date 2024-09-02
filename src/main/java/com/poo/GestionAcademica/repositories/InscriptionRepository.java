package com.poo.GestionAcademica.repositories;


import com.poo.GestionAcademica.entities.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Integer> {
}
