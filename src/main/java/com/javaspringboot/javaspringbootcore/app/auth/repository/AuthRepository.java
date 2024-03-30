package com.javaspringboot.javaspringbootcore.app.auth.repository;

import com.javaspringboot.javaspringbootcore.app.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth,Long> {

}
