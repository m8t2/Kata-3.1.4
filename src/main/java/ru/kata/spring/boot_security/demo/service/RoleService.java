package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface RoleService {

    List<Role> findAllRoles();

    Set<Role> findRolesByIds(List<Long> roleIds);

    void SetUserRoles (List<Long> roleIds, User user);
}
