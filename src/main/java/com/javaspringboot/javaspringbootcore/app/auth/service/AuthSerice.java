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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthSerice {
    private final AuthRepository authRepository;
    private final CryptoService cryptoService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Auth createUser(User user) {

        String code = cryptoService.generateVerificationCode();
        String token = jwtService.generateToken(user);

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setType(AuthType.VERIFY_SIGNUP);
        auth.setToken(token);
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

    public void verifySingIn(Long userId, String code) {

        Auth auth = authRepository.findByUserIdAndState(userId, AuthState.ACTIVE).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));
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

    }

    public Auth login(User user, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        String token = jwtService.generateToken(user);
        String code = cryptoService.generateVerificationCode();

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setType(AuthType.VERIFY_SIGNIN);
        auth.setToken(token);
        auth.setVerificationCode(code);
        return authRepository.save(auth);

    }


}
