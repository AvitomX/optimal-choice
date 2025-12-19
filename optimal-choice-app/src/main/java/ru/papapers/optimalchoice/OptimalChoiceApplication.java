package ru.papapers.optimalchoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OptimalChoiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OptimalChoiceApplication.class, args);
	}

}
