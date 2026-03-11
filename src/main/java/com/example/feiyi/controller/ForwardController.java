package com.example.feiyi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForwardController {

    @GetMapping({
        "/login",
        "/register",
        "/heritage",
        "/heritage/{id}",
        "/inheritors",
        "/inheritors/{id}",
        "/activities",
        "/activities/{id}",
        "/shop",
        "/shop/{id}",
        "/messages",
        "/about",
        "/search",
        "/dashboard",
        "/dashboard/**",
        "/user/**",
        "/cart",
        "/checkout",
        "/orders"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
