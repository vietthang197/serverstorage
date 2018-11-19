package com.levietthang.serverstorage.controller;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EntityScan(basePackages = {"com.levietthang.serverstorage", "com.levietthang.serverstorage.controller"})
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String defaulpage(){
        return "index";
    }
}
