package com.clothes.backend;

import com.clothes.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class ClothesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClothesBackendApplication.class, args);
	}

	@Bean
	public Hibernate5JakartaModule hibernate5Module() {
		return new Hibernate5JakartaModule();
	}

	@Bean
	public CommandLineRunner initUserActiveStatus(UserRepository userRepository) {
		return args -> {
			userRepository.findAll().forEach(user -> {
				user.setActive(true);
				userRepository.save(user);
			});
		};
	}
}
