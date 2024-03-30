package com.javaspringboot.javaspringbootcore.app.auth.service;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthType;
import com.javaspringboot.javaspringbootcore.app.auth.repository.AuthRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class AuthSerice {
    private final AuthRepository authRepository;

    public void createUser(User user, String token) {

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setState(AuthState.ACTIVE);
        auth.setType(AuthType.VERIFY_SIGNUP);
        auth.setToken(token);
        authRepository.save(auth);

    }
}
