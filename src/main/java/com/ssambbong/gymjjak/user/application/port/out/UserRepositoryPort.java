package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.domain.model.User;

public interface UserRepositoryPort {

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);
}
