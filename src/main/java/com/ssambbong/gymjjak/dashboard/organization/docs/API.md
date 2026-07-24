# 🏢 조직 대시보드 API

> 작성일: 2026-07-23
> 대상: `OrganizationDashboardController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/dashboard/organization`
> 권한: 모든 API는 `ORGANIZATION` 권한이 필요합니다.

## 기능 개요

- 조직 계정이 소속 헬스장의 통계, 매출, PT 목록, 트레이너별 현황, 수강생 목록을 조회합니다.

---

## 1. 헬스장 통계 조회

`GET /api/dashboard/organization/stats`

> 트레이너 수, 누적 이용자 수, 현재 이용자 수, 이번 달 매출, 기간별 이용자 추이를 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `DASH_200_STATS` | 헬스장 통계 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DASH_200_STATS",
  "message": "헬스장 통계 조회가 완료되었습니다.",
  "data": {
    "trainerCount": 5,
    "totalUserCount": 120,
    "currentUserCount": 48,
    "thisMonthRevenue": 3200000,
    "trend": {
      "weekly": [
        { "date": "2026-07-17", "count": 10 }
      ],
      "monthly": [
        { "date": "2026-07-01", "count": 48 }
      ],
      "threeMonthly": [
        { "date": "2026-05-01", "count": 42 }
      ],
      "sixMonthly": [
        { "date": "2026-02-01", "count": 35 }
      ]
    }
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.trainerCount` | 소속 트레이너 수 |
| `data.totalUserCount` | 누적 이용자 수 |
| `data.currentUserCount` | 현재 수강 중인 이용자 수 |
| `data.thisMonthRevenue` | 이번 달 매출 (원) |
| `data.trend.weekly` | 최근 1주 일별 이용자 추이 |
| `data.trend.monthly` | 최근 1개월 일별 이용자 추이 |
| `data.trend.threeMonthly` | 최근 3개월 월별 이용자 추이 |
| `data.trend.sixMonthly` | 최근 6개월 월별 이용자 추이 |
| `data.trend.[].date` | 기준 날짜 |
| `data.trend.[].count` | 해당 날짜/월의 이용자 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | - | - | 조직을 찾을 수 없음 |

---

## 2. 매출 관리 조회

`GET /api/dashboard/organization/sales`

> 누적 매출, 이번 달 매출, 전월 대비 증감률, 기간별 매출 추이, 트레이너별 매출을 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `DASH_200_SALES` | 매출 관리 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DASH_200_SALES",
  "message": "매출 관리 조회가 완료되었습니다.",
  "data": {
    "totalRevenue": 15000000,
    "thisMonthRevenue": 3200000,
    "monthOverMonthRate": 12.5,
    "revenueTrend": {
      "weekly": [
        { "date": "2026-07-17", "amount": 800000 }
      ],
      "monthly": [
        { "date": "2026-07-01", "amount": 3200000 }
      ],
      "threeMonthly": [
        { "date": "2026-05-01", "amount": 2800000 }
      ],
      "sixMonthly": [
        { "date": "2026-02-01", "amount": 2100000 }
      ]
    },
    "trainerSales": [
      {
        "trainerProfileId": 1,
        "trainerName": "김트레이너",
        "thisMonthAmount": 1200000,
        "totalAmount": 6000000,
        "ratio": 40.0
      }
    ]
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.totalRevenue` | 누적 총 매출 (원) |
| `data.thisMonthRevenue` | 이번 달 매출 (원) |
| `data.monthOverMonthRate` | 전월 대비 증감률 (%). 양수면 증가, 음수면 감소 |
| `data.revenueTrend.[].date` | 기준 날짜 |
| `data.revenueTrend.[].amount` | 해당 날짜/월의 매출 (원) |
| `data.trainerSales[].trainerProfileId` | 트레이너 프로필 ID |
| `data.trainerSales[].trainerName` | 트레이너 이름 |
| `data.trainerSales[].thisMonthAmount` | 이번 달 해당 트레이너 매출 (원) |
| `data.trainerSales[].totalAmount` | 해당 트레이너 누적 매출 (원) |
| `data.trainerSales[].ratio` | 전체 매출 중 해당 트레이너 비율 (%) |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | - | - | 조직을 찾을 수 없음 |

---

## 3. 조직 PT 목록 조회

`GET /api/dashboard/organization/pt-courses`

> 소속 트레이너들의 PT 과목 목록과 현재 수강생 수를 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `DASH_200_PT_COURSES` | 조직 PT 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DASH_200_PT_COURSES",
  "message": "조직 PT 목록 조회가 완료되었습니다.",
  "data": [
    {
      "ptCourseId": 1,
      "title": "가슴 집중 PT",
      "price": 100000,
      "totalSessionCount": 20,
      "status": "ACTIVE",
      "trainerName": "김트레이너",
      "currentStudentCount": 8
    }
  ]
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data[].ptCourseId` | PT 코스 ID |
| `data[].title` | PT 코스 이름 |
| `data[].price` | 수강료 (원) |
| `data[].totalSessionCount` | 총 세션 수 |
| `data[].status` | PT 코스 상태 |
| `data[].trainerName` | 담당 트레이너 이름 |
| `data[].currentStudentCount` | 현재 수강 중인 수강생 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | - | - | 조직을 찾을 수 없음 |

---

## 4. 트레이너별 통계 조회

`GET /api/dashboard/organization/trainers/clients`

> 소속 트레이너 목록을 누적 수강생 수 내림차순으로 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `DASH_200_TRAINER_STATS` | 트레이너별 통계 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DASH_200_TRAINER_STATS",
  "message": "트레이너별 통계 조회가 완료되었습니다.",
  "data": [
    {
      "trainerProfileId": 1,
      "trainerName": "김트레이너",
      "clientCount": 25,
      "ptCount": 3
    }
  ]
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data[].trainerProfileId` | 트레이너 프로필 ID |
| `data[].trainerName` | 트레이너 이름 |
| `data[].clientCount` | 누적 수강생 수 |
| `data[].ptCount` | 운영 중인 PT 코스 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | - | - | 조직을 찾을 수 없음 |

---

## 5. PT 수강생 목록 조회

`GET /api/dashboard/organization/pt-courses/{ptCourseId}/students`

> 특정 PT 코스의 현재 수강 중인 수강생 목록을 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 코스 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `DASH_200_PT_CLIENTS` | PT 수강생 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DASH_200_PT_CLIENTS",
  "message": "PT 수강생 목록 조회가 완료되었습니다.",
  "data": [
    {
      "userName": "홍길동",
      "enrolledAt": "2026-06-01T09:00:00",
      "progressCount": 8,
      "totalSessionCount": 20
    }
  ]
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data[].userName` | 수강생 이름 |
| `data[].enrolledAt` | 수강 등록 일시 |
| `data[].progressCount` | 현재까지 완료한 세션 수 |
| `data[].totalSessionCount` | 총 세션 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | - | - | 조직 또는 PT 코스를 찾을 수 없음 |

---

## 📝 문서 정보

- 작성일: `2026-07-23`
