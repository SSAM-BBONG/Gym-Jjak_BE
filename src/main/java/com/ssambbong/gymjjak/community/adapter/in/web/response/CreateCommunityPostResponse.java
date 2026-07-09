package com.ssambbong.gymjjak.community.adapter.in.web.response;

public record CreateCommunityPostResponse(

        Long communityPostId

) {

    public static CreateCommunityPostResponse from(
            Long communityPostId
    ) {
        return new CreateCommunityPostResponse(
                communityPostId
        );
    }
}