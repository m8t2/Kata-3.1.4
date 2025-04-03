package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.SecurityService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Hibernate.initialize(user.getRoles());
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
        Hibernate.initialize(user.getRoles());
        return user;
    }

    public User getName(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Transactional
    public void updateUser(User user, List<Long> roleIds) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setName(user.getName());
        existingUser.setSecondname(user.getSecondname());
        existingUser.setUsername(user.getUsername());
        existingUser.setAge(user.getAge());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword()); // Устанавливаем новый пароль
            securityService.setPasswordForUser(existingUser); // Кодируем его
        }

        Set<Role> updatedRoles = securityService.getRoles(roleIds);
        existingUser.getRoles().clear();
        existingUser.getRoles().addAll(updatedRoles);

        userRepository.save(existingUser);
    }

    @Override
    public boolean existsUser(Long id) {
        return userRepository.existsById(id);
    }

}