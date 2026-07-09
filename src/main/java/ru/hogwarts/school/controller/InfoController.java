package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final UserService userService;

    @Value("${server.port:port.default-8080}")
    private String servicePort;


    @GetMapping("/port")
    public String getServiceRort() {
        return ("Приложение запущено на порту: " + servicePort);
    }

    @GetMapping("/studentsSteamSorted")
    public ResponseEntity<List<String>> getStudentsSteamSorted(@RequestParam String sortedLetter) {
        return ResponseEntity.ok(userService.usersSteamSorted(sortedLetter));
    }
}
