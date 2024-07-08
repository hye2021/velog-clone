package org.example.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.blog.entity.Role;
import org.example.blog.entity.User;
import org.example.blog.repository.UserRepository;
import org.example.blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

        // password encoding
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
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
