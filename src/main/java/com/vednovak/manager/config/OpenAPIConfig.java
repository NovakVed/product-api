package com.vednovak.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI openApiInformation() {
                final Server localServer = new Server()
                                .url("http://localhost:8081")
                                .description("Localhost Server URL");

                final Contact contact = new Contact()
                                .email("vednovak@gmail.com")
                                .name("Vedran Novak")
                                .url("https://vednovak.com/");

                final Info info = new Info()
                                .contact(contact)
                                .title("Product API")
                                .description("""
                                                Product API: A robust solution for managing and organizing product data.
                                                This API provides endpoints for creating, updating, retrieving, and deleting product information,
                                                ensuring efficient and seamless product management.
                                                Additionally, the API supports returning product prices in other currencies supported by the application.
                                                It uses real-time exchange rates from official sources like the Hrvatska Narodna Banka API to ensure accuracy and optimization.""")
                                .summary("Product API: Manage and organize product data efficiently, with real-time currency conversion.")
                                .version("V1.0.0")
                                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

                return new OpenAPI().info(info).addServersItem(localServer);
        }
}
