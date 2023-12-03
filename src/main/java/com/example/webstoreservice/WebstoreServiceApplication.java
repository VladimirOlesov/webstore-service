package com.example.webstoreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WebstoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebstoreServiceApplication.class, args);
	}

}
