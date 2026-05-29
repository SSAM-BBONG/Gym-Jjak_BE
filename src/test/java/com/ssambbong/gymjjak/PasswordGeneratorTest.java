package com.ssambbong.gymjjak;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorTest {

    @Test
    void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("Test1234!");
        System.out.println("encoded: " + encoded);

        // 검증
        System.out.println("matches: " + encoder.matches("Test1234!", encoded));
    }
}