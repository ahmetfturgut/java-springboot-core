package com.javaspringboot.javaspringbootcore.app.user.service;

import com.javaspringboot.javaspringbootcore.app.user.enums.UserRole;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.repository.UserRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import com.javaspringboot.javaspringbootcore.core.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new ApiException(ApiError.USER_EMAIL_EXISTS);
        });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return user;

    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User login(User userDto) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD));

        if (!user.getState().equals(UserState.ACTIVE)) {
            throw new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD);
        }

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        user.setUsername(userDto.getUsername());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());

        userRepository.save(user);
        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));
    }


}
