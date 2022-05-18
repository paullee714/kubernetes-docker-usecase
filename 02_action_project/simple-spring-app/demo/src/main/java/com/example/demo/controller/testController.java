package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class testController {

    @GetMapping
    public String homeApi() {
        return "home API";
    }

    @GetMapping("/info")
    public String infoApi() {
        return "information API";
    }

}
