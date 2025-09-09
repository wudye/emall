package com.mwu.profileservice;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Profile Service API",
                version = "1.0",
                description = "API documentation for the Profile Service",
                contact =  @io.swagger.v3.oas.annotations.info.Contact(
                        name = "mwu",
                        email = "none"
                )

        )
)
public class Application {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        // 这会自动把 .env 里的变量注入到 System.getenv 和 System.getProperties
        System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }
}
