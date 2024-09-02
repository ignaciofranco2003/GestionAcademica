package com.poo.GestionAcademica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.poo.GestionAcademica")
@EntityScan(basePackages = "com.poo.GestionAcademica.entities")
public class GestionAcademicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionAcademicaApplication.class, args);
	}
}
