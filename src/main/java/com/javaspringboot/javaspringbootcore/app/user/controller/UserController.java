package com.javaspringboot.javaspringbootcore.app.user.controller;

import com.javaspringboot.javaspringbootcore.app.auth.service.AuthSerice;
import com.javaspringboot.javaspringbootcore.app.user.dto.*;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.service.UserService;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import com.javaspringboot.javaspringbootcore.core.response.ApiResponse;
import com.javaspringboot.javaspringbootcore.core.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthSerice authSerice;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("createUser")
    public ApiResponse<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {

        User user = userService.createUser(requestDto.toUser());

        String token = jwtService.generateToken(user);

        authSerice.createUser(user, token);

        return new ApiResponse<CreateUserResponseDto>(CreateUserResponseDto.builder().token(token).build());

    }

    @PostMapping("login")
    public ApiResponse<LoginUserResponseDto> login(@Valid @RequestBody LoginUserRequestDto requestDto) {

        User user = userService.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD));

        if (!user.getState().equals(UserState.ACTIVE)) {
            throw new ApiException(ApiError.WRONG_EMAIL_OR_PASSWORD);
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        String token = jwtService.generateToken(user);
        return new ApiResponse<LoginUserResponseDto>(LoginUserResponseDto.builder().token(token).build());


    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getUser(id);
        return new ApiResponse<User>(user);
    }

    @GetMapping()
    public ApiResponse<List<User>> getAllUser() {
        List<User> users = userService.getAllUser();
        return new ApiResponse<List<User>>(users);
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequestDto requestDto) {
        User user = userService.updateUser(id, requestDto.toUser());
        return new ApiResponse<User>(user);
    }


}
