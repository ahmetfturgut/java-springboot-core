package com.javaspringboot.javaspringbootcore.app.user.service;

 import com.javaspringboot.javaspringbootcore.app.user.enums.UserRole;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.repository.UserRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public User createUser(User user) {
        logger.info("Creating user with email: {}", user.getEmail());
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            logger.error("User creation failed, email already exists: {}", user.getEmail());
            throw new ApiException(ApiError.USER_EMAIL_EXISTS);
        });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);
        logger.info("User created with email: {}", user.getEmail());

        return user;
    }

    public void save(User user) {
        logger.info("Saving user with ID: {}", user.getId());
        userRepository.save(user);
    }

    public User login(User userDto) {
        logger.info("Login attempt for email: {}", userDto.getEmail());
        User user = userRepository.findByEmail(userDto.getEmail()).orElseGet(() -> {
            logger.error("Login failed, email not found: {}", userDto.getEmail());
            throw new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD);
        });

        if (!user.getState().equals(UserState.ACTIVE)) {
            logger.error("Login failed, account not active for email: {}", user.getEmail());
            throw new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD);
        }

        logger.info("Login successful for email: {}", user.getEmail());
        return user;
    }

    public Optional<User> findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUser() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.info("No users found");
        }
        return users;
    }

    public User updateUser(Long id, User userDto) {
        logger.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id).orElseGet(() -> {
            logger.error("Update failed, user not found for ID: {}", id);
            throw new ApiException(ApiError.USER_NOT_FOUND);
        });

        user.setUsername(userDto.getUsername());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());

        userRepository.save(user);
        logger.info("User with ID: {} successfully updated", id);
        return user;
    }

    public User getUser(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElseGet(() -> {
            logger.error("User not found with ID: {}", id);
            throw new ApiException(ApiError.USER_NOT_FOUND);
        });
    }


}
