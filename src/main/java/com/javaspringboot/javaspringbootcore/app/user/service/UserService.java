package com.javaspringboot.javaspringbootcore.app.user.service;

import com.javaspringboot.javaspringbootcore.app.user.repository.UserRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new ApiException(ApiError.USER_EMAIL_EXISTS);
        });
        return userRepository.save(user);

    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        user.setName(userDto.getName());
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
