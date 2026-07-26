# 📊 트레이너 시장동향 리포트 API

> 작성일: 2026-07-23
> 대상: `TrainerReportController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/trainer-reports`
> 권한: 모든 API는 `TRAINER` 권한이 필요하며, 본인 리포트만 조회 가능합니다.

## 기능 개요

- 트레이너가 AI가 생성한 본인의 시장동향 리포트 목록을 조회합니다.
- 특정 리포트의 상세 내용(텍스트 분석 + 시장 데이터 스냅샷)을 조회합니다.
- 리포트는 매월 자동 생성됩니다. 트레이너가 직접 생성할 수 없습니다.

---

## 1. 내 리포트 목록 조회

`GET /api/trainer-reports?page=0&size=20`

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (TRAINER 권한) |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `page` | X | 페이지 번호 (기본값: `0`, 0부터 시작) |
| `size` | X | 페이지 크기 (기본값: `20`) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `TRAINER_REPORT_200_LIST` | 트레이너 리포트 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_REPORT_200_LIST",
  "message": "트레이너 리포트 목록 조회 성공",
  "data": {
    "items": [
      {
        "trainerReportId": 3,
        "targetMonth": "2026-07-01"
      },
      {
        "trainerReportId": 2,
        "targetMonth": "2026-06-01"
      }
    ],
    "hasNext": false
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.items[].trainerReportId` | 리포트 ID |
| `data.items[].targetMonth` | 리포트 대상 월 (해당 월의 1일 날짜 형식, `yyyy-MM-dd`) |
| `data.hasNext` | 다음 페이지 존재 여부 |

> 목록은 최신순 정렬입니다.

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | TRAINER 권한 없음 |
| `404 Not Found` | - | - | 트레이너 프로필을 찾을 수 없음 |

---

## 2. 내 리포트 상세 조회

`GET /api/trainer-reports/{trainerReportId}`

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (TRAINER 권한) |

Path Variable

| name | description |
| --- | --- |
| `trainerReportId` | 조회할 리포트 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `TRAINER_REPORT_200_DETAIL` | 트레이너 리포트 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_REPORT_200_DETAIL",
  "message": "트레이너 리포트 상세 조회 성공",
  "data": {
    "trainerReportId": 3,
    "targetMonth": "2026-07-01",
    "report": "7월 헬스 트레이너 시장은 전월 대비 12% 성장했습니다...",
    "marketTrendsSnapshot": {
      "avgReviewRating": 4.3,
      "reviewCountDelta": 5,
      "avgPrice": 120000,
      "priceDelta": 5000,
      "trainerCountDelta": 2
    }
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.trainerReportId` | 리포트 ID |
| `data.targetMonth` | 리포트 대상 월 (`yyyy-MM-dd`, 해당 월의 1일) |
| `data.report` | AI가 생성한 시장동향 분석 텍스트 |
| `data.marketTrendsSnapshot` | 리포트 생성 당시의 시장 데이터 스냅샷 (JSON 원문). 프론트 차트 렌더링용 |
| `data.marketTrendsSnapshot.avgReviewRating` | 해당 월 평균 강사평 별점 |
| `data.marketTrendsSnapshot.reviewCountDelta` | 전월 대비 강사평 수 증감 |
| `data.marketTrendsSnapshot.avgPrice` | 해당 월 평균 PT 가격 (원) |
| `data.marketTrendsSnapshot.priceDelta` | 전월 대비 평균 PT 가격 증감 (원) |
| `data.marketTrendsSnapshot.trainerCountDelta` | 전월 대비 트레이너 수 증감 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | TRAINER 권한 없음 또는 본인 리포트가 아님 |
| `404 Not Found` | - | - | 리포트 또는 트레이너 프로필을 찾을 수 없음 |

---

## 📝 문서 정보

- 작성일: `2026-07-23`
