package com.javaspringboot.javaspringbootcore.app.user.controller;

import com.javaspringboot.javaspringbootcore.app.user.dto.UpdateUserRequestDto;
import com.javaspringboot.javaspringbootcore.app.user.service.UserService;
import com.javaspringboot.javaspringbootcore.app.user.dto.CreateUserRequestDto;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.core.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {

        User user = userService.createUser(requestDto.toUser());
        return new ResponseEntity<User>(user, CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getUser(id);
        return new ResponseEntity<User>(user, OK);
    }


    @GetMapping()
    public ApiResponse<List<User>> getAllUser() {
        List<User> users = userService.getAllUser();
        return new ApiResponse<List<User>>(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequestDto requestDto) {
        User user = userService.updateUser(id, requestDto.toUser());
        return new ResponseEntity<User>(user, OK);
    }


}
