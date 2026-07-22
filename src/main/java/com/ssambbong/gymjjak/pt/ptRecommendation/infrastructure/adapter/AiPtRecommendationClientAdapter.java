package com.ssambbong.gymjjak.pt.ptRecommendation.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.port.PtRecommendationAiPort;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationAiException;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@Slf4j
public class AiPtRecommendationClientAdapter implements PtRecommendationAiPort {

    private static final String PT_RECOMMENDATION_PATH = "/api/v1/pt-recommendations";

    private final RestClient restClient;

    public AiPtRecommendationClientAdapter(@Qualifier("aiServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<RecommendedCourse> recommend(
            List<CandidateCourse> candidates,
            Profile profile,
            boolean hasPain,
            String painArea,
            String painOnset
    ) {
        try {
            AiResponse response = restClient.post()
                    .uri(PT_RECOMMENDATION_PATH)
                    .body(AiRequest.from(candidates, profile, hasPain, painArea, painOnset))
                    .retrieve()
                    // 내부 인증 실패와 AI 서버의 그 외 오류는 사용자 인증 오류로 노출하지 않고 서버 간 장애로 처리한다.
                    .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                        log.warn("PT추천 AI 서버가 오류를 반환했습니다. status={}", httpResponse.getStatusCode());
                        throw new PtRecommendationAiException(PtRecommendationErrorCode.AI_SERVER_ERROR);
                    })
                    .body(AiResponse.class);

            if (response == null || response.recommendations() == null || response.recommendations().isEmpty()) {
                throw new PtRecommendationAiException(PtRecommendationErrorCode.INVALID_AI_RESULT);
            }
            return response.recommendations().stream().map(AiRecommendedCourse::toDomain).toList();
        } catch (PtRecommendationAiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.warn("PT추천 AI 서버 통신에 실패했습니다. exception={}", exception.toString(), exception);
            throw convertException(exception);
        }
    }

    private PtRecommendationAiException convertException(RestClientException exception) {
        // 동기 HTTP 호출의 연결 또는 응답 제한 시간 초과는 504 오류로 변환한다.
        if (exception instanceof ResourceAccessException && hasTimeoutCause(exception)) {
            return new PtRecommendationAiException(PtRecommendationErrorCode.AI_TIMEOUT, exception);
        }
        return new PtRecommendationAiException(PtRecommendationErrorCode.AI_SERVER_ERROR, exception);
    }

    private boolean hasTimeoutCause(Throwable exception) {
        Throwable cause = exception;
        while (cause != null) {
            String name = cause.getClass().getSimpleName().toLowerCase();
            if (name.contains("timeout")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    // FastAPI(Pydantic)는 snake_case 필드명을 기대하므로 전부 명시적으로 매핑한다.
    private record AiRequest(
            List<AiCandidateCourse> candidates,
            AiProfile profile,
            @JsonProperty("has_pain") boolean hasPain,
            @JsonProperty("pain_area") String painArea,
            @JsonProperty("pain_onset") String painOnset
    ) {
        static AiRequest from(
                List<CandidateCourse> candidates, Profile profile, boolean hasPain, String painArea, String painOnset
        ) {
            return new AiRequest(
                    candidates.stream().map(AiCandidateCourse::from).toList(),
                    AiProfile.from(profile),
                    hasPain, painArea, painOnset);
        }
    }

    private record AiCandidateCourse(
            @JsonProperty("course_id") Long courseId,
            @JsonProperty("course_name") String courseName,
            @JsonProperty("trainer_id") Long trainerId,
            @JsonProperty("trainer_name") String trainerName,
            String bio
    ) {
        static AiCandidateCourse from(CandidateCourse candidate) {
            return new AiCandidateCourse(
                    candidate.courseId(), candidate.courseName(),
                    candidate.trainerId(), candidate.trainerName(), candidate.bio());
        }
    }

    private record AiProfile(
            @JsonProperty("exercise_goal") String exerciseGoal,
            @JsonProperty("exercise_period") String exercisePeriod,
            @JsonProperty("exercise_frequency") String exerciseFrequency,
            @JsonProperty("pt_history_summary") String ptHistorySummary
    ) {
        static AiProfile from(Profile profile) {
            return new AiProfile(
                    profile.exerciseGoal(), profile.exercisePeriod(),
                    profile.exerciseFrequency(), profile.ptHistorySummary());
        }
    }

    private record AiResponse(List<AiRecommendedCourse> recommendations) {
    }

    private record AiRecommendedCourse(
            @JsonProperty("course_id") Long courseId,
            @JsonProperty("course_name") String courseName,
            @JsonProperty("trainer_id") Long trainerId,
            @JsonProperty("trainer_name") String trainerName,
            String reason
    ) {
        RecommendedCourse toDomain() {
            return new RecommendedCourse(courseId, courseName, trainerId, trainerName, reason);
        }
    }
}
