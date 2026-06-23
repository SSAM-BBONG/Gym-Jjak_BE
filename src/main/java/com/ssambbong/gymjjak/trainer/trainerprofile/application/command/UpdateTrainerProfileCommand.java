package com.ssambbong.gymjjak.trainer.trainerprofile.application.command;


import java.util.List;

public record UpdateTrainerProfileCommand(
    Long requesterId,
    ProfileImageUpdateAction profileImageAction,
    UpdateProfileImageFileCommand profileImageFile,
    List<String> additionalCertifications,
    List<String> awardHistories,
    String introduction
) {

    public UpdateTrainerProfileCommand {
        additionalCertifications =
                normalizeNullableList(additionalCertifications);

        awardHistories =
                normalizeNullableList(awardHistories);

        introduction = introduction == null
                ? null
                : introduction.trim();
    }

    // null -> 변화 없음, [] -> 전체 삭제
    private static List<String> normalizeNullableList(List<String> values) {
        if (values == null) {
            return null;
        }

        return values.stream()
                .map(String::trim)
                .distinct()
                .toList();
    }

}
