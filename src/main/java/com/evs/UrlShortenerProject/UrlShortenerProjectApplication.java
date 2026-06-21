package com.evs.UrlShortenerProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UrlShortenerProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerProjectApplication.class, args);
	}

}
