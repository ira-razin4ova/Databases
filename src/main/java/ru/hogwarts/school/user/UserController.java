package ru.hogwarts.school.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.PatchUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.dto.UserRequest;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive Long id) {
        return userService.getUserOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody PatchUserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable @Positive Long id,
                              @RequestBody User user) {
        user.setId(id);
        return userService.editUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUserByAge(@RequestParam LocalDate age) {
        return ResponseEntity.ok(userService.findByAge(age));
    }

    @GetMapping("/age")
    public ResponseEntity<List<UserDto>> getFindByAgeBetween(@RequestParam LocalDate from,
                                                             @RequestParam LocalDate to) {
        return ResponseEntity.ok(userService.findByAgeBetween(from, to));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv() {
        String data = userService.exportUserToCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "user.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<UserDto> getUserByIdDTO(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getByIdDTO(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

    @GetMapping("/limit")
    public ResponseEntity<List<UserDto>> getStudentLimit() {
        return ResponseEntity.ok(userService.getUserLimitFiveSortedDesc());
    }

    @GetMapping ("/search")
    public ResponseEntity<List <UserDto>> getSearch (UserRequest search) {
        return ResponseEntity.ok(userService.searchUsers(search));
    }

}
