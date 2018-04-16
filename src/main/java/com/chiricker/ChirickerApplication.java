package com.chiricker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class ChirickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChirickerApplication.class, args);
	}
}
