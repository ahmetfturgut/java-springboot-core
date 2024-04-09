package com.javaspringboot.javaspringbootcore.app.user.controller;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.service.AuthSerice;
import com.javaspringboot.javaspringbootcore.app.email.service.MailService;
import com.javaspringboot.javaspringbootcore.app.user.dto.*;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.service.UserService;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.dto.AuthendicatedUserResponseDto;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import com.javaspringboot.javaspringbootcore.core.response.ApiResponse;
 import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthSerice authSerice;
     private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("createUser")
    public ApiResponse<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        logger.info("User creation started");

        User user = userService.createUser(requestDto.toUser());

        Auth auth = authSerice.createUser(user);
        mailService.sendSignUpEmail(user, auth.getVerificationCode());
        logger.info("User creation completed for username: {}", user.getEmail());

        return new ApiResponse<CreateUserResponseDto>(CreateUserResponseDto.builder().token(auth.getToken()).build());

    }

    @PostMapping("verifySingUp")
    public ApiResponse<HttpStatus> verifySingUp(@Valid @RequestBody VerifySingUpRequestDto requestDto) {
        logger.info("SignUp verification started for token: {}", requestDto.getToken());

        String email = authSerice.extractUsername(requestDto.getToken());
        User user = userService.findByEmail(email).orElseGet(() -> {
            logger.error("User not found for email: {}", email);
            throw new ApiException(ApiError.USER_NOT_FOUND);
        });

        authSerice.verifySingUp(user.getId(), requestDto.getVerificationCode());
        user.setState(UserState.ACTIVE);
        userService.save(user);

        logger.info("SignUp verification completed for user: {}", email);

        return new ApiResponse<>(HttpStatus.OK);
    }

    @PostMapping("login")
    public ApiResponse<LoginUserResponseDto> login(@Valid @RequestBody LoginUserRequestDto requestDto) {
        logger.info("Login attempt for email: {}", requestDto.getEmail());

        User user = userService.login(requestDto.toUser());
        Auth auth = authSerice.verifySingIn(user, requestDto.getPassword());
        logger.info("Login successful for email: {}", user.getEmail());

        return new ApiResponse<LoginUserResponseDto>(LoginUserResponseDto.builder().token(auth.getToken()).build());

    }

    @GetMapping("signOut")
    public ApiResponse<HttpStatus> signOut() {
        logger.info("signOut started");
        User authentication = authSerice.getLoggedInUser();
        authSerice.singOut(authentication.getId());
        logger.info("signOut successful for email: {}", authentication.getEmail());

        return new ApiResponse<>(HttpStatus.OK);

    }

    @PostMapping("verifySingIn")
    public ApiResponse<VerifySignInResponseDto> verifySingIn(@Valid @RequestBody VerifySingInRequestDto requestDto) {
        logger.info("SignIn verification started for token: {}", requestDto.getToken());

        String email = authSerice.extractUsername(requestDto.getToken());
        User user = userService.findByEmail(email).orElseGet(() -> {
            logger.error("User not found for email: {}", email);
            throw new ApiException(ApiError.USER_NOT_FOUND);
        });

        Auth auth = authSerice.login(user, requestDto.getVerificationCode());
        user.setLastLoginDate(new Date());
        userService.save(user);

        VerifySignInResponseDto response = VerifySignInResponseDto.builder().authendicatedUserResponseDto(AuthendicatedUserResponseDto
                .builder()
                .id(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .surname(user.getSurname())
                .email(user.getEmail())
                .build()).token(auth.getToken()).build();

        logger.info("SignIn verification completed for user: {}", email);

        return new ApiResponse<VerifySignInResponseDto>(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable("id") Long userId) {
        logger.info("Fetching user with id: {}", userId);
        User user = userService.getUser(userId);
        if (user == null) {
            logger.error("User not found with id: {}", userId);
        }
        return new ApiResponse<>(user);
    }

    @GetMapping()
    public ApiResponse<List<User>> getAllUser() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUser();
        if (users.isEmpty()) {
            logger.info("No users found");
        }
        return new ApiResponse<>(users);
    }

    @PutMapping("/updateUser")
    public ApiResponse<User> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto) {
        logger.info("Attempt to update user with ID: {}", requestDto.getId());

        User authentication = authSerice.getLoggedInUser();
        Long id = Long.valueOf(requestDto.getId());

        if (!id.equals(authentication.getId())) {
            logger.error("Authorization error: Logged in user ID does not match the requested ID for update.");
            throw new ApiException(ApiError.NOT_AUTHORIZED);
        }

        User user = userService.updateUser(id, requestDto.toUser());
        logger.info("User with ID: {} successfully updated.", id);
        return new ApiResponse<>(user);
    }


}
