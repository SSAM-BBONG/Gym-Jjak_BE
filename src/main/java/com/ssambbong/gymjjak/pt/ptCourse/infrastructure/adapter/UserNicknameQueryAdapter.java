package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.UserNicknameQueryPort;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserNicknameQueryAdapter implements UserNicknameQueryPort {

    private final SpringDataUserRepository userRepository;

    // 목록 조회용 닉네임 배치 조회
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

    // 상세 조회용 단건 프로필 조회
    @Override
    public Optional<StudentProfile> findUserDetail(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new StudentProfile(
                        user.getNickname(),
                        user.getUsername(), // username = 이메일 로그인 ID
                        user.getPhone()
                ));
    }
}
