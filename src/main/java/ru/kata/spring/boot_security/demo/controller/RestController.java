package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.exception.JsonUserNotFound;
import ru.kata.spring.boot_security.demo.exception.JsonUserNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/people")
public class RestController {

    private final UserService userService;

    @Autowired
    public RestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("id") Long id) {
        User user = userService.findUser(id);

        if (user == null) {
            throw new JsonUserNotFound("User with id " + id + " not found");
        }

        return user;
    }
}
