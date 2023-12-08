package com.teachbeyond.bankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@OpenAPIDefinition(
        info = @Info(
                title = "The Java Bank Application",
                description = "Backend Rest APIs for My Bank",
                version = "v1.0",
                contact = @Contact(name = "Nikita Parmar", email = "nikita@gmail.com", url = ""),
                license = @License(name = "My Bank App", url = "")
        ),
        externalDocs = @ExternalDocumentation(
                description = "The Java Academy Bank App Documentation",
                url = ""
        )
)
public class BankApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}
