package com.mwu;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Appliation {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Appliation.class, args);
    }
}
