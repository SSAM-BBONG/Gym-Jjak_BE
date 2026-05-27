package com.ssambbong.gymjjak.user.adapter.out.security;

import com.ssambbong.gymjjak.user.application.port.out.PasswordEncodePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncodeAdapter implements PasswordEncodePort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
