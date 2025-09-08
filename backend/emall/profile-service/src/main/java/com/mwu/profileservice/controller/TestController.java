package com.mwu.profileservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/gatewaytest")
@RestController
public class TestController {

    @RequestMapping("/u" )
    public String test() {
        return "user Service is up and running";
    }
}
