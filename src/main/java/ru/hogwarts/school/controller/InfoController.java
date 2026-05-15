package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final StudentService studentService;

    @Value("${server.port:port.default-8080}")
    private String servicePort;


    @GetMapping("/port")
    public String getServiceRort() {
        return ("Приложение запущено на порту: " + servicePort);
    }

    @GetMapping("/studentsSteamSorted")
    public ResponseEntity<List<String>> getStudentsSteamSorted(@RequestParam String sortedLetter) {
        return ResponseEntity.ok(studentService.studentsSteamSorted(sortedLetter));
    }

    @GetMapping("/getAverageAge")
    public ResponseEntity<Double> getAverageAge() {
        return ResponseEntity.ok(studentService.getAverageAge());
    }

    @GetMapping("/getLongestFacultyName")
    public ResponseEntity<String> getLongestFacultyName() {
        return ResponseEntity.ok(studentService.getLongestFacultyName());
    }

    @GetMapping("/sum")
    public ResponseEntity<Long> getSum() {
        return ResponseEntity.ok(studentService.getSumOptimization());
    }
}
