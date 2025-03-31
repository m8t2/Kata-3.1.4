package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception.JsonUserNotFound;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/people")
@CrossOrigin(origins = "http://localhost:8000") // Разрешить запросы с фронтенда
public class RestController {

    private final UserService userService;

    @Autowired
    public RestController(UserService userService) {
        this.userService = userService;
    }

    // Обновленный RestController.java
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.findAllUsers();
        // Убедитесь, что роли загружаются (может потребоваться @EntityGraph или JOIN FETCH в репозитории)
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // RestController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
        }
    }

    // Обработка исключений
    @ExceptionHandler(JsonUserNotFound.class)
    public ResponseEntity<String> handleUserNotFoundException(JsonUserNotFound ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }
}