package com.soups.spring.web.discpsched;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DispSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DispSchedulerApplication.class, args);
	}
}
