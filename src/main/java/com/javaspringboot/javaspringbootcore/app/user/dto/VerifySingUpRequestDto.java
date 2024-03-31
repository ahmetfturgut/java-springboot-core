package com.javaspringboot.javaspringbootcore.app.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifySingUpRequestDto {

    @NotEmpty(message = "The token is required.")
    private String token;

    @NotEmpty(message = "The verificationCode is required.")
    private String verificationCode;
}
