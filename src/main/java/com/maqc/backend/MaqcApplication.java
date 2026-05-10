package com.maqc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaqcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaqcApplication.class, args);
	}

}
