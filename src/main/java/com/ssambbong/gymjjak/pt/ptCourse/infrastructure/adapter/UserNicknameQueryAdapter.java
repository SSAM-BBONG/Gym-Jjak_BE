package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.UserNicknameQueryPort;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserNicknameQueryAdapter implements UserNicknameQueryPort {

    private final SpringDataUserRepository userRepository;


    @Override
    public Map<Long, String> findNicknamesByUserIds(List<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();

        // userId 목록으로 유저 배치 조회 후 userId → nickname Map 변환
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        user -> user.getId(),
                        user -> user.getNickname()
                ));
    }
}
