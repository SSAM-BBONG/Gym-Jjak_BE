package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

public interface UserNicknameQueryPort {

    // userId 목록별 닉네임 배치 조회 (N+1 방지)
    // 존재하지 않는 userId는 Map에 포함되지 않음
    Map<Long, String> findNicknamesByUserIds(List<Long> userIds);
}
