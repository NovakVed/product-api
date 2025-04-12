package com.vednovak.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // TODO: fix this! change text!!!
    @Bean
    public OpenAPI openApiInformation() {
        final Server localServer = new Server()
                .url("http://localhost:8081")
                .description("Localhost Server URL");

        final Contact contact = new Contact()
                .email("vednovak@gmail.com")
                .name("Vedran Novak");

        final Info info = new Info()
                .contact(contact)
                .title("Product API")
                .description("""
                        Product API: Shorten and manage long URLs efficiently for convenient sharing and tracking.
                        Build the Spring Boot application with maven: mvn clean package (in case error: "command not found: mvn" please install maven -> macOS: brew install maven)
                        Run the Spring boot application with the java -jar <jar_file_name> command: java -jar target/url-shortener-0.0.1-SNAPSHOT.jar""")
                .summary("Product API: Shorten and manage long URLs efficiently for convenient sharing and tracking.")
                .version("V1.0.0")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        return new OpenAPI().info(info).addServersItem(localServer);
    }
}
