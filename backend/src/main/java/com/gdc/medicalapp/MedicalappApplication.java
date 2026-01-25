package com.gdc.medicalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicalappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalappApplication.class, args);
	}

}
