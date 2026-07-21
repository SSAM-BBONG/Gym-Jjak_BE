# 🏁 온보딩 API

> 작성일: 2026-07-21  
> 대상: `OnboardingController`  
> 공통 경로: `/api/onboarding`  
> 공통 응답 형식: `status`, `code`, `message`, `data`

## 기능 개요

인증된 사용자가 운동 목적·기간·빈도·선호 운동, 신체 정보와 선호 지역을 등록하고 조회·수정하는 API입니다. 모든 API는 Bearer Access Token이 필요하며 SecurityConfig상 `USER`, `ADMIN` 권한이 접근할 수 있습니다.

## 공통 요청 데이터

등록과 수정 요청은 같은 구조를 사용합니다.

```json
{
  "exerciseGoal": "체중 감량",
  "exercisePeriod": "6개월",
  "exerciseFrequency": "주 3회",
  "preferredExercise": "헬스",
  "height": 175.5,
  "weight": 72.3,
  "region": {
    "sido": "서울",
    "sigungu": "강남구",
    "eupmyeondong": "역삼동",
    "fullName": "서울 강남구 역삼동 테헤란로 123",
    "latitude": 37.5665,
    "longitude": 127.9780
  }
}
```

| 필드 | 필수 | 검증 및 의미 |
| --- | --- | --- |
| `exerciseGoal` | O | 공백이 아닌 운동 목적 |
| `exercisePeriod` | O | 공백이 아닌 운동 기간 |
| `exerciseFrequency` | O | 공백이 아닌 운동 빈도 |
| `preferredExercise` | O | 공백이 아닌 선호 운동 |
| `height` | O | 0보다 큰 숫자 |
| `weight` | O | 0보다 큰 숫자 |
| `region` | O | 선호 지역 객체 |
| `region.sido` | O | 시·도 |
| `region.sigungu` | O | 시·군·구 |
| `region.eupmyeondong` | O | 읍·면·동 |
| `region.fullName` | O | 전체 주소 문자열 |
| `region.latitude` | O | 위도 |
| `region.longitude` | O | 경도 |

---

## 1. 온보딩 등록

`POST /api/onboarding/me`

로그인한 사용자의 첫 온보딩 설문과 선호 지역을 등록합니다. 사용자 ID는 요청 본문이 아니라 Access Token의 인증 주체에서 가져옵니다.

### 성공 응답

```json
{
  "status": 201,
  "code": "ONBOARDING_201_001",
  "message": "온보딩 등록이 완료되었습니다.",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 필수값 누락, 공백 문자열, 키·몸무게가 0 이하인 경우 |
| `401 Unauthorized` | `COMMON_401` | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 허용된 권한이 아닌 경우 |
| `404 Not Found` | `USER_404` | 인증 ID에 해당하는 사용자가 없는 경우 |
| `409 Conflict` | `USER_409_004` | 이미 온보딩 설문이 등록된 경우 |

---

## 2. 내 온보딩 조회

`GET /api/onboarding/me`

### 성공 응답

```json
{
  "status": 200,
  "code": "ONBOARDING_200_001",
  "message": "온보딩이 조회되었습니다.",
  "data": {
    "onboardingId": 10,
    "exerciseGoal": "체중 감량",
    "exercisePeriod": "6개월",
    "exerciseFrequency": "주 3회",
    "preferredExercise": "헬스",
    "preferredRegion": {
      "regionId": 20,
      "sido": "서울",
      "sigungu": "강남구",
      "eupmyeondong": "역삼동",
      "fullName": "서울 강남구 역삼동 테헤란로 123",
      "latitude": 37.5665,
      "longitude": 127.9780
    },
    "height": 175.5,
    "weight": 72.3
  }
}
```

등록된 설문이 없으면 `404 Not Found`, code `ONBOARDING_404`를 반환합니다.

---

## 3. 내 온보딩 수정

`PUT /api/onboarding/me`

등록 API와 동일한 전체 요청 구조를 사용합니다. 부분 수정이 아니므로 설문과 지역의 모든 필드를 전달해야 합니다.

### 성공 응답

```json
{
  "status": 200,
  "code": "ONBOARDING_200_002",
  "message": "온보딩이 수정되었습니다.",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 요청값 검증에 실패한 경우 |
| `404 Not Found` | `USER_404` | 사용자가 없는 경우 |
| `404 Not Found` | `ONBOARDING_404` | 수정할 온보딩이 없는 경우 |
| `404 Not Found` | `REGION_404` | 기존 선호 지역을 찾을 수 없는 경우 |

---

## 문서 정보

- 업데이트일: `2026-07-21`
- 현재 Controller, Request/Response DTO와 도메인 오류 코드를 기준으로 작성했습니다.

