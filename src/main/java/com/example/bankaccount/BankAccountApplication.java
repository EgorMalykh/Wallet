package com.example.bankaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class BankAccountApplication {

	public static void main(String[] args) {
        SpringApplication.run(BankAccountApplication.class, args);
	}

}
