package com.javaspringboot.javaspringbootcore.core.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@AllArgsConstructor
@Service
public class CryptoService {

    public String generateVerificationCode() {
        Random random = new Random();
        int min = 100000;
        int max = 1000000;
        int verificationCode = random.nextInt(max - min) + min;
        return String.valueOf(verificationCode);
    }
}
