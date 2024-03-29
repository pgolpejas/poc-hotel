package com.reservation.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "info")
@Setter
public class OpenApiConfiguration {
    private String title;
    private String description;
    private String version;

    @Bean
    public OpenAPI apiCustomizer() {
        Contact contact = new Contact();
        contact.setEmail("pgolpejas@gmail.com");
        contact.setName("Pedro Sánchez Álvarez");

        Info info = new Info()
            .title(title)
            .version(version)
            .description(description)
            .contact(contact);

        return new OpenAPI().info(info);
    }
}
