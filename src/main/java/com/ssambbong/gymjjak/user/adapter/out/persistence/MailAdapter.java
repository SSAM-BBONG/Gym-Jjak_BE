package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.application.port.out.MailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class MailAdapter implements MailPort {

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final int PASSWORD_LENGTH = 12;

    private final SecureRandom random = new SecureRandom();

    @Async("mailTaskExecutor")
    @Override
    public void sendTemporaryPassword(String email, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("[짐짝] 임시 비밀번호 안내");
        message.setText("""
                안녕하세요. 짐짝입니다.

                임시 비밀번호가 발급되었습니다.

                임시 비밀번호: %s

                로그인 후 반드시 비밀번호를 변경해주세요.
                """.formatted(temporaryPassword));

        javaMailSender.send(message);
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public String generate() {
        StringBuilder password = new StringBuilder();

        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        for (int i = password.length(); i < PASSWORD_LENGTH; i++) {
            password.append(randomChar(ALL));
        }

        return shuffle(password.toString());
    }

    private char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
    }

    private String shuffle(String value) {
        char[] chars = value.toCharArray();

        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }





}
