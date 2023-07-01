package com.dh.keycloak;

import com.dh.keycloak.service.KeycloakClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

@SpringBootApplication
public class KeycloakApplication implements CommandLineRunner {

	@Autowired
	private KeycloakClientService keycloakClientService;

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Variables
		String reino = "facturacion-realm";
		String client = "facturacion-client";
		String clientSecret = "facturacion-secret";
		String gwClient = "gateway-client";
		String gwClientSecret = "gateway-secret";
		List<String> roles = List.of("admin", "user");
		String grupo = "PROVIDERS";
		String scope = "roles";

		// Se crea el reino
		keycloakClientService.createRealm(reino);
		// Creamos client
		keycloakClientService.createClient(reino,
				client,
				clientSecret,
				roles);
		// Creamos client para el gateway
		keycloakClientService.createGatewayClient(reino,
				gwClient,
				gwClientSecret,
				roles);
		// Creamos grupo PROVIDERS
		keycloakClientService.createGroup(reino, grupo);
		// Asignamos grupo al token
		keycloakClientService.addGroupsToToken(reino, scope);

		// Paramos aplicacion luego de crear lo necesario
		System.exit(0);
	}

}
