package com.ssambbong.gymjjak.community.application.command;

import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;

public record CreateCommunityPostCommand(

        Long userId,
        String role,
        CommunityPostType type,
        String title,
        String content

) {
}