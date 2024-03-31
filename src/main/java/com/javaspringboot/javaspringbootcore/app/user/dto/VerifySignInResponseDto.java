package com.javaspringboot.javaspringbootcore.app.user.dto;

import com.javaspringboot.javaspringbootcore.core.dto.AuthendicatedUserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifySignInResponseDto {
    private String token;
    private AuthendicatedUserResponseDto authendicatedUserResponseDto;
}
