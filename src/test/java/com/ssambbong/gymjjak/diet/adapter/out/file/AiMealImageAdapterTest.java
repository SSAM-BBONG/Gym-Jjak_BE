package com.ssambbong.gymjjak.diet.adapter.out.file;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiMealImageAdapterTest {
    @Mock private FileRepository fileRepository;
    @Mock private FileStoragePort fileStoragePort;
    @InjectMocks private AiMealImageAdapter adapter;

    @Test
    void 본인이_업로드한_식단_이미지의_임시_조회_URL을_반환한다() {
        File file = mealImage(15L, 10L);
        when(fileRepository.findById(15L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(file.getFileUrl()))
                .thenReturn("https://s3.example.com/meal?signature=test");

        String url = adapter.resolveAccessibleImageUrl(15L, 10L);

        assertThat(url).isEqualTo("https://s3.example.com/meal?signature=test");
    }

    @Test
    void 다른_사용자의_식단_이미지는_사용할_수_없다() {
        when(fileRepository.findById(15L)).thenReturn(Optional.of(mealImage(15L, 10L)));

        assertThatThrownBy(() -> adapter.resolveAccessibleImageUrl(15L, 20L))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    void 다른_용도로_등록한_이미지는_식단_분석에_사용할_수_없다() {
        File profileImage = File.restore(
                15L, 10L, "profile.jpg", "stored", "uploads/profiles/trainers/10/stored",
                "image/jpeg", 1024L, FileType.PROFILE_IMAGE);
        when(fileRepository.findById(15L)).thenReturn(Optional.of(profileImage));

        assertThatThrownBy(() -> adapter.resolveAccessibleImageUrl(15L, 10L))
                .isInstanceOf(InvalidFileException.class);
    }

    private File mealImage(Long fileId, Long uploaderId) {
        return File.restore(
                fileId, uploaderId, "meal.jpg", "stored", "uploads/meals/" + uploaderId + "/stored",
                "image/jpeg", 1024L, FileType.MEAL_IMAGE);
    }
}
