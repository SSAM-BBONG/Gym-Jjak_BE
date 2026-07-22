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
class MealImageUrlAdapterTest {

    @Mock private FileRepository fileRepository;
    @Mock private FileStoragePort fileStoragePort;
    @InjectMocks private MealImageUrlAdapter adapter;

    @Test
    void 식단_소유자의_이미지_URL을_발급한다() {
        File file = mealImage(15L, 20L);
        when(fileRepository.findById(15L)).thenReturn(Optional.of(file));
        when(fileStoragePort.getPresignedUrl(file.getFileUrl())).thenReturn("https://s3.example/meal");

        assertThat(adapter.resolve(15L, 20L)).isEqualTo("https://s3.example/meal");
    }

    @Test
    void 식단_소유자와_파일_업로더가_다르면_거부한다() {
        when(fileRepository.findById(15L)).thenReturn(Optional.of(mealImage(15L, 30L)));

        assertThatThrownBy(() -> adapter.resolve(15L, 20L))
                .isInstanceOf(FileAccessDeniedException.class);
    }

    @Test
    void 식단_이미지가_아닌_파일은_거부한다() {
        File profileImage = File.restore(
                15L, 20L, "profile.jpg", "stored", "uploads/profiles/20/stored",
                "image/jpeg", 1024L, FileType.PROFILE_IMAGE);
        when(fileRepository.findById(15L)).thenReturn(Optional.of(profileImage));

        assertThatThrownBy(() -> adapter.resolve(15L, 20L))
                .isInstanceOf(InvalidFileException.class);
    }

    private File mealImage(Long fileId, Long uploaderId) {
        return File.restore(
                fileId, uploaderId, "meal.jpg", "stored", "uploads/meals/" + uploaderId + "/stored",
                "image/jpeg", 1024L, FileType.MEAL_IMAGE);
    }
}
