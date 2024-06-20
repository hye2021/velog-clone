package org.example.blog.service;

import org.example.blog.entity.Role;
import org.example.blog.entity.User;
import org.example.blog.repository.UserRepository;
import org.example.blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUsersByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));

        // ROLE_USER 역할 지정
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            throw new RuntimeException("Error: Role [ROLE_USER] is not found.");
        }
        var user_roles = user.getRoles();
        user_roles.add(role);
        user.setRoles(user_roles);

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkPassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
