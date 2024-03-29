package com.pocspringbootkafka.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "info")
public class OpenApiConfiguration {
    private String title;
    private String description;
    private String version;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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
