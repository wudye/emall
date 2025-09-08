package com.mwu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/gatewaytest")
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Gateway Service is up and running";
    }


}
