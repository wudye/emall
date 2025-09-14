package com.mwu.eureka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/eu")
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Eureka Service is up and running!";
    }
}
