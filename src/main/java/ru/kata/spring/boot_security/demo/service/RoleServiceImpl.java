package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Set<Role> findRolesByIds(List<Long> roleIds) {
        return roleRepository.findByIdIn(roleIds != null ? roleIds : List.of());
    }

    public void SetUserRoles (List<Long> roleIds, User user) {
        Set<Role> roles = findRolesByIds(roleIds);
        user.setRoles(roles);
    }
}
