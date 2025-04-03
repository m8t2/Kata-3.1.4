package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.List;


@Controller
public class MainController {

    private final UserServiceImpl userServiceImpl;
    private final RoleServiceImpl roleService;
    private final UserServiceImpl userService;

    @Autowired
    public MainController(UserServiceImpl userDetailsService, RoleServiceImpl roleService, UserServiceImpl userService) {
        this.userServiceImpl = userDetailsService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String test() {
        return "index";
    }



}
