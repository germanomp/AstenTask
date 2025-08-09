package com.astentask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AstentaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstentaskApplication.class, args);
	}

}
