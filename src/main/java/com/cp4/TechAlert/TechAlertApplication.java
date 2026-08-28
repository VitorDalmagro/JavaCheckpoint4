package com.cp4.TechAlert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TechAlertApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechAlertApplication.class, args);
		System.out.println("Aplicação iniciada com sucesso");
		System.out.println("Acesse: http://localhost:8080/api/filmes/buscar?titulo=Matrix");
	}

}
