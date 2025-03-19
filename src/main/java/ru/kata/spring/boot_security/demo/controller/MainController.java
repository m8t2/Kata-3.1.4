package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.CustomUserDetailsService;

import javax.validation.Valid;
import java.util.List;


@Controller
public class MainController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final RoleRepository roleRepository;

    @Autowired
    public MainController(UserRepository userRepository, CustomUserDetailsService userDetailsService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<User> users = userDetailsService.findAllUsers();
        model.addAttribute("usersDetails", userDetails);
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        return "admin";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails);
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("secondname", user.getSecondname());
        return "user";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        User user = userDetailsService.findUser(id);
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/update";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "update";
        }
        userDetailsService.updateUser(user);
        return "redirect:/admin";
    }

}
