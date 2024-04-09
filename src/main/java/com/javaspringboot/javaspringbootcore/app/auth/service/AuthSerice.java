package com.javaspringboot.javaspringbootcore.app.auth.service;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthType;
import com.javaspringboot.javaspringbootcore.app.auth.repository.AuthRepository;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.config.CryptoService;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class AuthSerice {
    private final AuthRepository authRepository;
    private final CryptoService cryptoService;
    private Environment environment;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthSerice.class);


    public Auth createUser(User user) {
        int verifySignUpExpiresIn = Optional.ofNullable(environment.getProperty("javaspringbootore.app.verifySignUpExpiresIn", Integer.class)).orElse(86400000);
        String code = cryptoService.generateVerificationCode();
        String token = generateToken(user, verifySignUpExpiresIn, AuthType.VERIFY_SIGNUP);

        Auth auth = new Auth();
        auth.setUser(user);
        auth.setType(AuthType.VERIFY_SIGNUP);
        auth.setToken(token);
        auth.setExpiresIn(new Date(System.currentTimeMillis() + verifySignUpExpiresIn));
        auth.setVerificationCode(code);

        return authRepository.save(auth);

    }

    public void verifySingUp(Long userId, String code) {

        Auth auth = authRepository.findByUserIdAndStateAndType(userId, AuthState.ACTIVE, AuthType.VERIFY_SIGNUP).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));
        if (auth == null) {
            logger.error("Auth not found");
            throw new ApiException(ApiError.TOKEN_ERROR);
        }

        if (!auth.getState().equals(AuthState.ACTIVE)) {
            logger.error("Auth state not active" + "auth.state: " + auth.getState());
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getVerificationCode().equals(code)) {
            logger.error("Auth code not correct" + "code: " + auth.getVerificationCode());
            throw new ApiException(ApiError.TOKEN_CODE_ERROR);
        }
        auth.setState(AuthState.PASSIVE);
        authRepository.save(auth);

    }

    public void checkAndGetAuthenticatedUser(String token) {

        if (token == null) {
            logger.error("token is null");
            throw new ApiException(ApiError.TOKEN_ERROR);
        }
        Auth auth = authRepository.findByTokenAndState(token, AuthState.ACTIVE).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        if (auth == null) {
            logger.error("Auth not found");
            throw new ApiException(ApiError.TOKEN_ERROR);
        }

        if (auth.getType() != AuthType.SIGNIN) {
            logger.error("Auth type not correct" + "auth.type: " + auth.getType());
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }
        authRepository.updateLastRequestDateById(auth.getId());


    }

    public Auth login(User user, String code) {

        Auth auth = authRepository.findByUserIdAndStateAndType(user.getId(), AuthState.ACTIVE, AuthType.VERIFY_SIGNIN).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        if (auth == null) {
            logger.error("Auth not found");
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getState().equals(AuthState.ACTIVE)) {
            logger.error("Auth state not active" + "auth.state: " + auth.getState());
            throw new ApiException(ApiError.TOKEN_EXPIRED);
        }

        if (!auth.getVerificationCode().equals(code)) {
            logger.error("Auth code not correct" + "code: " + auth.getVerificationCode());
            throw new ApiException(ApiError.TOKEN_CODE_ERROR);
        }
        auth.setState(AuthState.PASSIVE);
        authRepository.save(auth);

        int verifySignInExpiresIn = Optional.ofNullable(environment.getProperty("authExpiresIn.app.verifySignInExpiresIn", Integer.class)).orElse(86400000);
        String token = generateToken(user, verifySignInExpiresIn, AuthType.SIGNIN);

        Auth singInAuth = new Auth();
        singInAuth.setUser(user);
        singInAuth.setType(AuthType.SIGNIN);
        singInAuth.setExpiresIn(new Date(System.currentTimeMillis() + verifySignInExpiresIn));
        singInAuth.setToken(token);

        authRepository.save(singInAuth);
        return singInAuth;
    }

    public Auth verifySingIn(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.debug("User password not validated.");
            throw new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD);
        }
        int authExpiresIn = Optional.ofNullable(environment.getProperty("authExpiresIn.app.verifySignUpExpiresIn", Integer.class)).orElse(86400000);
        String token = generateToken(user, authExpiresIn, AuthType.VERIFY_SIGNIN);
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

    public String extractUsername(String token) {
        return exportToken(token, Claims::getSubject);
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();

        return claimsTFunction.apply(claims);
    }

    private SecretKey getKey() {
        byte[] key = Decoders.BASE64.decode(environment.getProperty("security.jwt.secret", String.class));
        return Keys.hmacShaKeyFor(key);
    }

    public boolean tokenControl(String jwt, String email) {
        final String userEmail = extractUsername(jwt);
        return (userEmail.equals(email) && !exportToken(jwt, Claims::getExpiration).before(new Date()));
    }

    public String generateToken(User user, Integer expiresIn, AuthType type) {

        authRepository.updateAuthTypeByUserIdAndState(user.getId(), AuthState.PASSIVE, type);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiresIn))
                .claim("authType", type)
                .signWith(getKey())
                .compact();
    }

    public void singOut(Long userId) {
        authRepository.updateAuthTypeByUserIdAndState(userId, AuthState.PASSIVE, AuthType.SIGNIN);
    }


}
