package com.javaspringboot.javaspringbootcore.app.auth.repository;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthType;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUserIdAndStateAndType(Long userId, AuthState state, AuthType type);

    Optional<Auth> findByTokenAndState(String token, AuthState state);

    @Modifying
    @Transactional
    @Query("UPDATE Auth a SET a.lastRequestDate = CURRENT_TIMESTAMP WHERE a.id = :id")
    void updateLastRequestDateById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Auth a SET a.type = :type WHERE a.user.id = :userId AND a.state = :state")
    void updateAuthTypeByUserIdAndState(Long userId, AuthState state, AuthType type);


    @Modifying
    @Transactional
    @Query("UPDATE Auth a SET a.state = :newState WHERE a.user.id = :userId")
    void updateAuthStateByUserId(Long userId, AuthState newState);


}
