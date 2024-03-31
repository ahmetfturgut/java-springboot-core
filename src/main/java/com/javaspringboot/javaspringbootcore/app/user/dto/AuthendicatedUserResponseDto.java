package com.javaspringboot.javaspringbootcore.app.user.dto;

import com.javaspringboot.javaspringbootcore.app.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthendicatedUserResponseDto {

    private Long id;
    private String email;
    private String username;
    private String surname;
    private UserRole role;

}
