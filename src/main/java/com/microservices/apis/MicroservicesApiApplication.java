package com.microservices.apis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
public class MicroservicesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicesApiApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
	}


	public void addCorsMappings(CorsRegistry registry){
		registry.addMapping("/usuario/**")
		.allowedMethods("*")
		.allowedOrigins("*");
	}

}


