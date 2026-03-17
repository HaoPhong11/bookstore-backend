package com.example.bookstore_db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@SpringBootApplication
public class BookstoreDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookstoreDbApplication.class, args);
	}
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
