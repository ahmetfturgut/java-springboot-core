package com.javaspringboot.javaspringbootcore.app.user.dto;

import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequestDto {

    @NotEmpty(message = "The email email is required.")
    @Email(message = "The email email is invalid.", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String email;

    @NotEmpty(message = "The password is required.")
    @Size(min = 6, max = 12, message = "The length of full name must be between 6 and 12 characters.")
    private String password;

    public User toUser() {
        return new User().setEmail(email).setPassword(password);

    }
}
