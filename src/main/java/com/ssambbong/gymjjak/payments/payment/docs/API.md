# 💳 결제 API

> 작성일: 2026-07-21
> 대상: `PaymentController`, `PaymentWebhookController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 결제 생성·조회 API는 `USER` 또는 `TRAINER` 권한이 필요합니다. 웹훅 API는 인증이 불필요합니다.

## 기능 개요

- 💳 사용자는 PT 강습 또는 구독 플랜에 대한 결제를 요청합니다. 서버는 `orderId`와 `amount`를 반환하고, 프론트엔드가 PortOne V2 SDK로 실제 결제를 진행합니다.
- 🔔 PortOne이 결제 결과를 웹훅으로 전달하면 서버가 PortOne API를 호출해 금액·상태를 검증한 뒤 결제 상태를 확정합니다.
- 📋 사용자는 본인의 결제 내역 목록과 특정 PT 강습의 구매 여부를 조회할 수 있습니다.

---

## 1. PT 결제 요청

`POST /api/payments/pt`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Request Body

```json
{
  "ptCourseId": 1
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `ptCourseId` | O | 결제할 PT 강습 ID입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `PAYMENT_PT_CREATED` | PT 결제 요청이 생성되었습니다. |

Response Body

```json
{
  "status": 201,
  "code": "PAYMENT_PT_CREATED",
  "message": "PT 결제 요청이 생성되었습니다",
  "data": {
    "orderId": "PT-05YJQK1Z0GY4R",
    "amount": 150000
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.orderId` | 서버가 생성한 주문번호입니다. PortOne V2 SDK 호출 시 `paymentId`로 사용합니다. |
| `data.amount` | 결제 금액입니다. PortOne SDK 호출 시 `amount`로 사용합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `404 Not Found` | `PAYMENT_005` | 결제 대상을 찾을 수 없습니다. | 요청한 PT 강습 ID가 존재하지 않는 경우 |
| `409 Conflict` | `PAYMENT_001` | 이미 구매한 PT 코스입니다. | 동일 PT 강습을 이미 결제 완료한 경우 |

---

## 2. 구독 결제 요청

`POST /api/payments/subscriptions`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Request Body

```json
{
  "planType": "MONTHLY"
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `planType` | O | 구독 플랜 타입입니다. `MONTHLY` 또는 `YEARLY` 중 하나입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `PAYMENT_SUBSCRIPTION_CREATED` | 구독 결제 요청이 생성되었습니다. |

Response Body

```json
{
  "status": 201,
  "code": "PAYMENT_SUBSCRIPTION_CREATED",
  "message": "구독 결제 요청이 생성되었습니다",
  "data": {
    "orderId": "SUB-05YJQK1Z0GY4R",
    "amount": 7900
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.orderId` | 서버가 생성한 주문번호입니다. PortOne V2 SDK 호출 시 `paymentId`로 사용합니다. |
| `data.amount` | 결제 금액입니다. PortOne SDK 호출 시 `amount`로 사용합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `planType`이 유효하지 않은 값인 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `409 Conflict` | `PAYMENT_004` | 이미 활성 구독이 존재합니다. | 현재 활성 구독 또는 처리 중인 구독 결제가 있는 경우 |

---

## 3. 내 결제 내역 목록 조회

`GET /api/payments/me`

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
| `200 OK` | `PAYMENTS_FETCHED` | 결제 내역 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PAYMENTS_FETCHED",
  "message": "결제 내역 조회 성공",
  "data": {
    "payments": [
      {
        "productType": "PT",
        "itemName": "가슴 집중 PT 3회",
        "amount": 150000,
        "status": "PAID",
        "processedAt": "2026-07-12T21:53:12"
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.payments` | 결제 내역 목록입니다. 최신순으로 정렬됩니다. |
| `data.payments[].productType` | 상품 유형입니다. `PT` 또는 `SUBSCRIPTIONS` 중 하나입니다. |
| `data.payments[].itemName` | PT이면 강습명, 구독이면 플랜 타입입니다. |
| `data.payments[].amount` | 결제 금액입니다. |
| `data.payments[].status` | 결제 상태입니다. `PENDING` / `PAID` / `CANCELLED` / `FAILED` 중 하나입니다. |
| `data.payments[].processedAt` | 상태별 처리 시각입니다. `PAID`→결제완료일, `CANCELLED`→취소일, `FAILED`→실패일, `PENDING`→`null`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 4. PT 구매 상태 조회

`GET /api/payments/pt-courses/{ptCourseId}/my-status`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 강습 ID입니다. |

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
| `200 OK` | `PT_PURCHASE_STATUS_FETCHED` | PT 구매 상태 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_PURCHASE_STATUS_FETCHED",
  "message": "PT 구매 상태 조회 성공",
  "data": {
    "isPurchased": true
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.isPurchased` | 해당 PT 강습의 유효한 구매 여부입니다. `PAID` 상태인 결제가 존재하면 `true`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 5. PortOne 웹훅 수신

`POST /api/payments/webhook`

PortOne이 결제 이벤트 발생 시 서버로 직접 전송합니다. 인증 토큰이 필요하지 않습니다. 서버가 200을 반환하지 않으면 PortOne이 웹훅을 재전송합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Content-Type` | `application/json` |
| `webhook-id` | PortOne이 부여한 웹훅 이벤트 고유 ID입니다. 시그니처 검증에 사용됩니다. |
| `webhook-timestamp` | 웹훅 발송 시각입니다 (Unix 초). 시그니처 검증 및 재전송 공격 방어에 사용됩니다. |
| `webhook-signature` | PortOne이 계산한 서명값입니다. `v1,{Base64(HMAC-SHA256)}` 형식입니다. |

Request Body

```json
{
  "type": "Transaction.Paid",
  "data": {
    "paymentId": "PT-0QZHBKQN83C5Z",
    "transactionId": "portone-tx-0QZHBKQN83C5Z"
  }
}
```

| name | 설명 |
| --- | --- |
| `type` | 웹훅 이벤트 타입입니다. `Transaction.Paid` / `Transaction.Failed` / `Transaction.Cancelled` 중 하나입니다. |
| `data.paymentId` | 서버가 생성한 주문 ID입니다 (PortOne V2에서 merchantPaymentId = 우리 orderId). |
| `data.transactionId` | PortOne 거래 고유 ID입니다. DB의 `transaction_id` 컬럼에 저장됩니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `WEBHOOK_RECEIVED` | 웹훅 처리 완료 |

Response Body

```json
{
  "status": 200,
  "code": "WEBHOOK_RECEIVED",
  "message": "웹훅 처리 완료",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `PAYMENT_006` | 웹훅 시그니처 검증에 실패했습니다. | 서명 헤더 누락, timestamp 5분 초과, 서명값 불일치인 경우 |
| `404 Not Found` | `PAYMENT_002` | 결제 정보를 찾을 수 없습니다. | `orderId`에 해당하는 결제 정보가 없는 경우 |
| `500 Internal Server Error` | `PAYMENT_003` | PortOne API 호출에 실패했습니다. | PortOne 결제 검증 API 호출에 실패한 경우 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
