package com.mwu;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Appication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Appication.class, args);
    }
}
