# 관리자 대시보드 API

Base URL: `/api/dashboard/admin`

모든 API는 `ADMIN` 권한이 필요하다.

## 공통 요청 헤더

| name | description |
| --- | --- |
| `Authorization` | 필수. `Bearer {accessToken}` 형식의 JWT Access Token |
| `Content-Type` | `application/json` |

## 회원 현황 조회

### Request

```http
GET /api/dashboard/admin/members
```

Request Parameter: 없음

### Response

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `ADMIN_DASHBOARD_200_1` | 관리자 대시보드 회원 현황 조회 성공 |

```json
{
  "status": 200,
  "code": "ADMIN_DASHBOARD_200_1",
  "message": "관리자 대시보드 회원 현황 조회에 성공했습니다.",
  "data": {
    "totalUserCount": 120,
    "totalTrainerCount": 18,
    "totalOrganizationCount": 9,
    "monthlyUserSignups": [
      {
        "month": "2026-02",
        "count": 12
      }
    ]
  }
}
```

- `monthlyUserSignups`는 이번 달을 포함한 최근 6개 달력 월을 반환한다.
- 가입자가 없는 월도 `count: 0`으로 포함한다.

## 콘텐츠 현황 조회

### Request

```http
GET /api/dashboard/admin/contents
```

Request Parameter: 없음

### Response

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `ADMIN_DASHBOARD_200_3` | 관리자 대시보드 콘텐츠 현황 조회 성공 |

```json
{
  "status": 200,
  "code": "ADMIN_DASHBOARD_200_3",
  "message": "관리자 대시보드 콘텐츠 현황 조회에 성공했습니다.",
  "data": {
    "activePtCourseCount": 35,
    "blindedPtCourseCount": 2,
    "pendingReportGroupCount": 4
  }
}
```

## 월별 예약 PT 수 조회

> 구현 위치: `ptReservation` 패키지

### Request

```http
GET /api/dashboard/admin/reservations
```

Request Parameter: 없음

Request Body: 없음

### Response

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `PT_RESERVATION_200_5` | 관리자 대시보드 월별 예약 PT 수 조회 성공 |

```json
{
  "status": 200,
  "code": "PT_RESERVATION_200_5",
  "message": "관리자 대시보드 월별 예약 PT 수 조회에 성공.",
  "data": {
    "monthlyReservations": [
      {
        "month": "2026-02",
        "count": 0
      },
      {
        "month": "2026-03",
        "count": 0
      },
      {
        "month": "2026-04",
        "count": 0
      },
      {
        "month": "2026-05",
        "count": 0
      },
      {
        "month": "2026-06",
        "count": 12
      },
      {
        "month": "2026-07",
        "count": 0
      }
    ]
  }
}
```

| name | 설명 |
| --- | --- |
| `status` | HTTP 상태 코드 |
| `code` | 서비스 응답 코드 |
| `message` | 응답 메시지 |
| `data.monthlyReservations` | 최근 6개월 월별 예약 PT 수 목록 |
| `data.monthlyReservations[].month` | 예약 월. `yyyy-MM` 형식 |
| `data.monthlyReservations[].count` | 해당 월의 예약 PT 수. 예약이 없으면 `0` |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `AUTH_401_001` | Access Token이 없습니다. | Access Token이 없거나 인증에 실패한 경우 |
| `403 Forbidden` | `AUTH_403_001` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

## 매출 통계 조회

### Request

```http
GET /api/dashboard/admin/revenues
```

Request Parameter: 없음

### Response

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | 구현 시 확정 | 관리자 대시보드 매출 통계 조회 성공 |

```json
{
  "status": 200,
  "code": "ADMIN_DASHBOARD_200_4",
  "message": "관리자 매출 통계를 조회했습니다.",
  "data": {
    "monthlyRevenues": [
      {
        "month": "2026-02",
        "ptCommissionRevenue": 150000,
        "subscriptionRevenue": 89000,
        "totalRevenue": 239000
      },
      {
        "month": "2026-03",
        "ptCommissionRevenue": 0,
        "subscriptionRevenue": 0,
        "totalRevenue": 0
      }
    ]
  }
}
```

- `monthlyRevenues`는 이번 달을 포함한 최근 6개 달력 월을 반환한다.
- 집계 대상은 `payments.status = PAID`이며, 월 귀속 기준은 `payments.paid_at`이다.
- `ptCommissionRevenue`는 월별 PT 결제금액 합계에 10%를 적용하고 원 단위 `HALF_UP`으로 반올림한 값이다.
- `subscriptionRevenue`는 월별 구독권 결제금액 합계다.
- 결제가 없는 달은 모든 금액을 `0`으로 포함한다.

## 공통 실패 응답

| HTTP 상태 | 설명 |
| --- | --- |
| `401 Unauthorized` | 인증되지 않은 요청 |
| `403 Forbidden` | 관리자 권한이 없는 요청 |
| `500 Internal Server Error` | 예상하지 못한 서버 오류 |
