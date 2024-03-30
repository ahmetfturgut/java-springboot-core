package com.javaspringboot.javaspringbootcore.app.user.dto;

import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequestDto {
    @NotEmpty(message = "The full name is required.")
    @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters.")
    private String name;

    @NotEmpty(message = "The full surname is required.")
    @Size(min = 2, max = 100, message = "The length of full surname must be between 2 and 100 characters.")
    private String surname;

    @NotEmpty(message = "The email email is required.")
    @Email(message = "The email email is invalid.", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String email;

    @NotEmpty(message = "The password is required.")
    @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters.")
    private String password;
    public User toUser() {
        return new User().setName(name).setSurname(surname).setEmail(email).setPassword(password);

    }
}
