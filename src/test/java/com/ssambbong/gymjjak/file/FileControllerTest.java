package com.ssambbong.gymjjak.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.presentation.api.FileController;
import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FileUseCase fileUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private Authentication auth;

    @BeforeEach
    void setUp() {
        auth = new UsernamePasswordAuthenticationToken(
                new AuthUser(1L, "user1", "USER"), null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

    @Nested
    @DisplayName("POST /api/files/presigned-urls - Presigned URL 일괄 발급")
    class GeneratePresignedUrls {

        @Test
        @DisplayName("성공 시 presigned URL 목록을 반환한다")
        void success() throws Exception {
            when(fileUseCase.generatePresignedUploadUrls(anyList()))
                    .thenReturn(List.of(
                            new PresignedUrlResult("https://s3.amazonaws.com/presigned1", "uploads/organizations/1/uuid1"),
                            new PresignedUrlResult("https://s3.amazonaws.com/presigned2", "uploads/profiles/trainers/1/uuid2")
                    ));

            String body = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(
                            Map.of("fileType", "BUSINESS_LICENSE", "contentType", "application/pdf"),
                            Map.of("fileType", "PROFILE_IMAGE", "contentType", "image/jpeg")
                    )
            ));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.files").isArray())
                    .andExpect(jsonPath("$.data.files.length()").value(2))
                    .andExpect(jsonPath("$.data.files[0].presignedUrl").value("https://s3.amazonaws.com/presigned1"))
                    .andExpect(jsonPath("$.data.files[0].fileKey").value("uploads/organizations/1/uuid1"));
        }

        @Test
        @DisplayName("files가 비어있으면 400을 반환한다")
        void fail_emptyFiles() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("files", Collections.emptyList()));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("files가 10개를 초과하면 400을 반환한다")
        void fail_tooManyFiles() throws Exception {
            List<Map<String, String>> files = Collections.nCopies(11,
                    Map.of("fileType", "BUSINESS_LICENSE", "contentType", "application/pdf"));

            String body = objectMapper.writeValueAsString(Map.of("files", files));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("fileType이 null이면 400을 반환한다")
        void fail_nullFileType() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(Map.of("contentType", "application/pdf"))
            ));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("contentType이 비어있으면 400을 반환한다")
        void fail_blankContentType() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(Map.of("fileType", "BUSINESS_LICENSE", "contentType", ""))
            ));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("허용되지 않는 contentType이면 400을 반환한다")
        void fail_invalidContentType() throws Exception {
            when(fileUseCase.generatePresignedUploadUrls(anyList()))
                    .thenThrow(new InvalidFileException(FileErrorCode.FILE_INVALID_TYPE));

            String body = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(Map.of("fileType", "BUSINESS_LICENSE", "contentType", "video/mp4"))
            ));

            mockMvc.perform(post("/api/files/presigned-urls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(authentication(auth)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/files/{fileId}/presigned-url - 파일 조회 URL 발급")
    class GetPresignedUrl {

        @Test
        @DisplayName("성공 시 조회 URL을 반환한다")
        void success() throws Exception {
            when(fileUseCase.getPresignedUrl(any()))
                    .thenReturn("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz");

            mockMvc.perform(get("/api/files/1/presigned-url")
                            .with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("https://s3.amazonaws.com/bucket/key?X-Amz-Signature=xyz"));
        }

        @Test
        @DisplayName("접근 권한이 없으면 403을 반환한다")
        void fail_accessDenied() throws Exception {
            doThrow(new FileAccessDeniedException()).when(fileUseCase).getPresignedUrl(any());

            mockMvc.perform(get("/api/files/1/presigned-url")
                            .with(authentication(auth)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 파일이면 404를 반환한다")
        void fail_notFound() throws Exception {
            doThrow(new FileNotFoundException(999L)).when(fileUseCase).getPresignedUrl(any());

            mockMvc.perform(get("/api/files/999/presigned-url")
                            .with(authentication(auth)))
                    .andExpect(status().isNotFound());
        }
    }
}
