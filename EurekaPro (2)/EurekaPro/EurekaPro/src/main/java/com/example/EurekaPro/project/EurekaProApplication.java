package com.example.EurekaPro.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaProApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaProApplication.class, args);
	}

}
