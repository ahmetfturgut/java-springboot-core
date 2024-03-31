package com.javaspringboot.javaspringbootcore.app.auth.service;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthType;
import com.javaspringboot.javaspringbootcore.app.auth.repository.AuthRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.config.CryptoService;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import com.javaspringboot.javaspringbootcore.core.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthSerice {
    private final AuthRepository authRepository;
    private final CryptoService cryptoService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private Environment environment;


    public Auth createUser(User user) {
        int verifySignUpExpiresIn = Optional.ofNullable(environment.getProperty("javaspringbootore.app.verifySignUpExpiresIn", Integer.class)).orElse(86400000);
        String code = cryptoService.generateVerificationCode();
        String token = jwtService.generateToken(user, verifySignUpExpiresIn);

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setType(AuthType.VERIFY_SIGNUP);
        auth.setToken(token);
        auth.setExpiresIn(new Date(System.currentTimeMillis() + verifySignUpExpiresIn));
        auth.setVerificationCode(code);

        return authRepository.save(auth);

    }

    public void verifySingUp(Long userId, String code) {

        Auth auth = authRepository.findByUserIdAndState(userId, AuthState.ACTIVE).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));
        if (!auth.getType().equals(AuthType.VERIFY_SIGNUP)) {
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getState().equals(AuthState.ACTIVE)) {
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getVerificationCode().equals(code)) {
            throw new ApiException(ApiError.TOKEN_CODE_ERROR);
        }
        auth.setState(AuthState.PASSIVE);
        authRepository.save(auth);

    }

    public Auth login(User user, String code) {

        Auth auth = authRepository.findByUserIdAndState(user.getId(), AuthState.ACTIVE).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        if (!auth.getType().equals(AuthType.VERIFY_SIGNIN)) {
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getState().equals(AuthState.ACTIVE)) {
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getVerificationCode().equals(code)) {
            throw new ApiException(ApiError.TOKEN_CODE_ERROR);
        }
        auth.setState(AuthState.PASSIVE);
        authRepository.save(auth);

        int verifySignInExpiresIn = Optional.ofNullable(environment.getProperty("authExpiresIn.app.verifySignInExpiresIn", Integer.class)).orElse(86400000);
        String token = jwtService.generateToken(user, verifySignInExpiresIn);

        Auth singInAuth = new Auth();
        singInAuth.setUser(user);
        singInAuth.setType(AuthType.SIGNIN);
        singInAuth.setExpiresIn(new Date(System.currentTimeMillis() + verifySignInExpiresIn));
        singInAuth.setToken(token);

        authRepository.save(singInAuth);
        return singInAuth;
    }

    public Auth verifySingIn(User user, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        int authExpiresIn = Optional.ofNullable(environment.getProperty("authExpiresIn.app.verifySignUpExpiresIn", Integer.class)).orElse(86400000);
        String token = jwtService.generateToken(user, authExpiresIn);
        String code = cryptoService.generateVerificationCode();

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setType(AuthType.VERIFY_SIGNIN);
        auth.setToken(token);
        auth.setExpiresIn(new Date(System.currentTimeMillis() + authExpiresIn));
        auth.setVerificationCode(code);

        return authRepository.save(auth);

    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            throw new ApiException(ApiError.USER_NOT_FOUND);
        }
    }


}
