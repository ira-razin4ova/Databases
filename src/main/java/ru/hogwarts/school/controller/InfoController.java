package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @Value("${server.port:port.default-8080}")
    private String servicePort;


    @GetMapping("/port")
    public String getServiceRort() {
        return ("Приложение запущено на порту: " + servicePort);
    }
}
