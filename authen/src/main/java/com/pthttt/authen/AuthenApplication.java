package com.pthttt.authen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthenApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenApplication.class, args);
	}

}
