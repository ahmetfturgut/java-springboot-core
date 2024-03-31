package com.javaspringboot.javaspringbootcore.app.user.controller;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.service.AuthSerice;
import com.javaspringboot.javaspringbootcore.app.user.dto.*;
import com.javaspringboot.javaspringbootcore.app.user.enums.UserState;
import com.javaspringboot.javaspringbootcore.app.user.service.UserService;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.dto.AuthendicatedUserResponseDto;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import com.javaspringboot.javaspringbootcore.core.response.ApiResponse;
import com.javaspringboot.javaspringbootcore.core.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthSerice authSerice;
    private final JwtService jwtService;

    @PostMapping("createUser")
    public ApiResponse<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {

        User user = userService.createUser(requestDto.toUser());
        //TODO: Email send
        Auth auth = authSerice.createUser(user);

        return new ApiResponse<CreateUserResponseDto>(CreateUserResponseDto.builder().token(auth.getToken()).build());

    }

    @PostMapping("verifySingUp")
    public ApiResponse<HttpStatus> verifySingUp(@Valid @RequestBody VerifySingUpRequestDto requestDto) {
        String email = jwtService.extractUsername(requestDto.getToken());
        User user = userService.findByEmail(email).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

        authSerice.verifySingUp(user.getId(), requestDto.getVerificationCode());
        user.setState(UserState.ACTIVE);
        userService.save(user);
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PostMapping("login")
    public ApiResponse<LoginUserResponseDto> login(@Valid @RequestBody LoginUserRequestDto requestDto) {

        User user = userService.login(requestDto.toUser());
        Auth auth = authSerice.verifySingIn(user, requestDto.getPassword());
        return new ApiResponse<LoginUserResponseDto>(LoginUserResponseDto.builder().token(auth.getToken()).build());

    }

    @PostMapping("verifySingIn")
    public ApiResponse<VerifySignInResponseDto> verifySingIn(@Valid @RequestBody VerifySingInRequestDto requestDto) {
        String email = jwtService.extractUsername(requestDto.getToken());
        User user = userService.findByEmail(email).orElseThrow(() -> new ApiException(ApiError.USER_NOT_FOUND));

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

        return new ApiResponse<VerifySignInResponseDto>(response);
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

    @PutMapping("/updateUser")
    public ApiResponse<User> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto) {
        User authentication = authSerice.getLoggedInUser();
        Long id = Long.valueOf(requestDto.getId());

        if (id.equals(authentication.getId())) {
            throw new ApiException(ApiError.NOT_AUTHORIZED);
        }

        User user = userService.updateUser(id, requestDto.toUser());
        return new ApiResponse<User>(user);
    }


}
