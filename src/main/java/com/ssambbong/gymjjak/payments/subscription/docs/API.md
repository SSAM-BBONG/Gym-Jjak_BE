# 🔔 구독 API

> 작성일: 2026-07-21
> 대상: `SubscriptionController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 플랜 목록 조회는 인증이 불필요합니다. 내 구독 조회는 `USER` 또는 `TRAINER` 권한이 필요합니다.

## 기능 개요

- 📋 구독 플랜 목록(월간·연간)은 누구나 조회할 수 있습니다.
- 🔍 로그인한 사용자는 본인의 활성 구독 정보를 조회할 수 있습니다.
- 구독 **생성**은 결제 웹훅(`POST /api/payments/webhook`) 처리 시 자동으로 이루어집니다. 직접 생성 API는 없습니다.

---

## 1. 구독 플랜 목록 조회

`GET /api/subscriptions/plans`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Request Body

```json

```

| name | 설명 |
| --- | --- |
| - | 본 API는 Request Body를 사용하지 않습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `SUBSCRIPTION_PLANS_FETCHED` | 구독 플랜 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "SUBSCRIPTION_PLANS_FETCHED",
  "message": "구독 플랜 목록 조회 성공",
  "data": {
    "plans": [
      {
        "planType": "MONTHLY",
        "price": 7900
      },
      {
        "planType": "YEARLY",
        "price": 79000
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.plans` | 구독 플랜 목록입니다. |
| `data.plans[].planType` | 플랜 유형입니다. `MONTHLY` 또는 `YEARLY` 중 하나입니다. |
| `data.plans[].price` | 가격입니다. 단위는 원(KRW)입니다. |

---

## 2. 내 구독 조회

`GET /api/subscriptions/me`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Request Body

```json

```

| name | 설명 |
| --- | --- |
| - | 본 API는 Request Body를 사용하지 않습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `SUBSCRIPTION_FETCHED` | 내 구독 조회 성공 |

Response Body

활성 구독이 있는 경우:

```json
{
  "status": 200,
  "code": "SUBSCRIPTION_FETCHED",
  "message": "내 구독 조회 성공",
  "data": {
    "planType": "MONTHLY",
    "status": "ACTIVE",
    "startedAt": "2026-07-01T00:00:00",
    "expiredAt": "2026-08-01T00:00:00"
  }
}
```

활성 구독이 없는 경우 `data`는 `null`을 반환합니다.

### Response Field

| name | 설명 |
| --- | --- |
| `data.planType` | 플랜 유형입니다. `MONTHLY` 또는 `YEARLY` 중 하나입니다. |
| `data.status` | 구독 상태입니다. `ACTIVE` 또는 `EXPIRED` 중 하나입니다. |
| `data.startedAt` | 구독 시작 일시입니다. |
| `data.expiredAt` | 구독 만료 일시입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
