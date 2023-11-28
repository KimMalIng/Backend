package com.capstone.AreyouP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AreyouPApplication {

	public static void main(String[] args) {
		SpringApplication.run(AreyouPApplication.class, args);
	}

}