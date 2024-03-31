package com.javaspringboot.javaspringbootcore.app.auth.repository;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth,Long> {

    Optional<Auth> findByUserIdAndState(Long userId, AuthState state);


}
