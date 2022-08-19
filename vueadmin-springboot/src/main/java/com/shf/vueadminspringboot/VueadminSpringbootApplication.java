package com.shf.vueadminspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class VueadminSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(VueadminSpringbootApplication.class, args);
	}

}
