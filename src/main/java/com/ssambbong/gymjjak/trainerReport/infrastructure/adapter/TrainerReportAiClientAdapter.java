package com.ssambbong.gymjjak.trainerReport.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort;
import com.ssambbong.gymjjak.trainerReport.domain.exception.TrainerReportAiException;
import com.ssambbong.gymjjak.trainerReport.domain.exception.TrainerReportErrorCode;
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
public class TrainerReportAiClientAdapter implements TrainerReportAiPort {
    private static final String TRAINER_REPORT_PATH = "/trainer-report";

    private final RestClient restClient;

    public TrainerReportAiClientAdapter(@Qualifier("aiServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String generateReport(Long trainerId, MarketTrendsSnapshot marketTrends, List<MyPtCourseSnapshot> myPtCourses) {
        try {
            AiResponse response = restClient.post()
                    .uri(TRAINER_REPORT_PATH)
                    .body(AiRequest.from(trainerId, marketTrends, myPtCourses))
                    .retrieve()
                    // 내부 인증 실패와 AI 서버의 그 외 오류는 사용자 인증 오류로 노출하지 않고 서버 간 장애로 처리한다.
                    .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                        log.warn("트레이너 리포트 AI 서버가 오류를 반환했습니다. status={}", httpResponse.getStatusCode());
                        throw new TrainerReportAiException(TrainerReportErrorCode.AI_SERVER_ERROR);
                    })
                    .body(AiResponse.class);

            if (response == null || response.report() == null || response.report().isBlank()) {
                throw new TrainerReportAiException(TrainerReportErrorCode.INVALID_AI_RESULT);
            }
            return response.report();
        } catch (TrainerReportAiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.warn("트레이너 리포트 AI 서버 통신에 실패했습니다. trainerId={}, exception={}",
                    trainerId, exception.toString(), exception);
            throw convertException(exception);
        }
    }

    private TrainerReportAiException convertException(RestClientException exception) {
        // 동기 HTTP 호출의 연결 또는 응답 제한 시간 초과는 504 오류로 변환한다.
        if (exception instanceof ResourceAccessException && hasTimeoutCause(exception)) {
            return new TrainerReportAiException(TrainerReportErrorCode.AI_TIMEOUT, exception);
        }
        return new TrainerReportAiException(TrainerReportErrorCode.AI_SERVER_ERROR, exception);
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

    private record AiRequest(
            @JsonProperty("trainer_id") Long trainerId,
            @JsonProperty("market_trends") MarketTrendsDto marketTrends,
            @JsonProperty("my_pt_courses") List<MyPtCourseDto> myPtCourses
    ) {
        static AiRequest from(Long trainerId, MarketTrendsSnapshot trends, List<MyPtCourseSnapshot> courses) {
            return new AiRequest(
                    trainerId,
                    MarketTrendsDto.from(trends),
                    courses.stream().map(MyPtCourseDto::from).toList());
        }
    }

    private record MarketTrendsDto(
            @JsonProperty("popular_body_parts") List<BodyPartTrendDto> popularBodyParts,
            @JsonProperty("price_distribution") List<PriceDistributionDto> priceDistribution,
            @JsonProperty("price_per_session_distribution") List<PriceDistributionDto> pricePerSessionDistribution,
            @JsonProperty("session_count_distribution") List<SessionCountDistributionDto> sessionCountDistribution,
            @JsonProperty("location_distribution") List<LocationDistributionDto> locationDistribution
    ) {
        static MarketTrendsDto from(MarketTrendsSnapshot trends) {
            return new MarketTrendsDto(
                    trends.popularBodyParts().stream().map(BodyPartTrendDto::from).toList(),
                    trends.priceDistribution().stream().map(PriceDistributionDto::from).toList(),
                    trends.pricePerSessionDistribution().stream().map(PriceDistributionDto::from).toList(),
                    trends.sessionCountDistribution().stream().map(SessionCountDistributionDto::from).toList(),
                    trends.locationDistribution().stream().map(LocationDistributionDto::from).toList());
        }
    }

    private record BodyPartTrendDto(
            @JsonProperty("body_part") String bodyPart,
            double percentage,
            @JsonProperty("percentage_change_from_last_month") Double percentageChangeFromLastMonth
    ) {
        static BodyPartTrendDto from(BodyPartTrendSnapshot snapshot) {
            return new BodyPartTrendDto(
                    snapshot.bodyPart(), snapshot.percentage(), snapshot.percentageChangeFromLastMonth());
        }
    }

    private record PriceDistributionDto(
            @JsonProperty("price_range") String priceRange,
            @JsonProperty("min_price") int minPrice,
            @JsonProperty("max_price") Integer maxPrice,
            double percentage,
            @JsonProperty("percentage_change_from_last_month") Double percentageChangeFromLastMonth
    ) {
        static PriceDistributionDto from(PriceDistributionSnapshot snapshot) {
            return new PriceDistributionDto(
                    snapshot.priceRange(), snapshot.minPrice(), snapshot.maxPrice(),
                    snapshot.percentage(), snapshot.percentageChangeFromLastMonth());
        }
    }

    private record SessionCountDistributionDto(
            @JsonProperty("session_count") int sessionCount,
            double percentage
    ) {
        static SessionCountDistributionDto from(SessionCountDistributionSnapshot snapshot) {
            return new SessionCountDistributionDto(snapshot.sessionCount(), snapshot.percentage());
        }
    }

    private record LocationDistributionDto(String region, double percentage) {
        static LocationDistributionDto from(LocationDistributionSnapshot snapshot) {
            return new LocationDistributionDto(snapshot.region(), snapshot.percentage());
        }
    }

    private record MyPtCourseDto(
            String name,
            int price,
            @JsonProperty("session_count") int sessionCount,
            @JsonProperty("body_part") String bodyPart
    ) {
        static MyPtCourseDto from(MyPtCourseSnapshot snapshot) {
            return new MyPtCourseDto(
                    snapshot.name(), snapshot.price(), snapshot.sessionCount(), snapshot.bodyPart());
        }
    }

    private record AiResponse(String report) {
    }
}
