package org.junbetterway.multithreading.lockdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LockdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockdemoApplication.class, args);
	}

}
