package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception.JsonUserNotFound;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.security.SecurityService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/people")
@CrossOrigin(origins = "http://localhost:8000") // Разрешить запросы с фронтенда
public class RestController {

    private final UserService userService;
    private final RoleService roleService;
    private final SecurityService securityService;

    @Autowired
    public RestController(UserService userService,
                          RoleService roleService,
                          SecurityService securityService) {
        this.userService = userService;
        this.roleService = roleService;
        this.securityService = securityService;
    }

    // Добавить новый метод для получения ролей
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam long id) {
        User user = userService.findUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
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

    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setSecondname(userDTO.getSecondname());
        user.setUsername(userDTO.getUsername());
        user.setAge(userDTO.getAge());
        user.setPassword(userDTO.getPassword());

        Set<Role> roles = roleService.findRolesByIds(userDTO.getRoleIds());
        user.setRoles(roles);

        securityService.setPasswordForUser(user);
        userService.addUser(user);

        return ResponseEntity.ok(user);
    }


    // Обработка исключений
    @ExceptionHandler(JsonUserNotFound.class)
    public ResponseEntity<String> handleUserNotFoundException(JsonUserNotFound ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }
}