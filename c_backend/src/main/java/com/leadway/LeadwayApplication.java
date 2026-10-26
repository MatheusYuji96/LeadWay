package com.leadway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Sistema de Acompanhamento de Lead",
				version = "1.0",
				description = "CRM focado em cadastro, acompanhamento, qualificação e contato com possíveis clientes em modalidade B2B."
		)
)
@SpringBootApplication
public class LeadwayApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadwayApplication.class, args);
	}

}
