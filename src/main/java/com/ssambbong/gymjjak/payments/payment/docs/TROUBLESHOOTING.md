# 💳 결제 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 로그 이벤트 키로 검색하면 원인을 빠르게 추적할 수 있습니다.

---

## 1. 웹훅이 400으로 거부된다

`PortOneWebhookVerifier.verify()`에서 던지는 `WebhookInvalidSignatureException` → `PAYMENT_006` (400)

### 원인별 로그

| 로그 이벤트 | 원인 | 조치 |
|---|---|---|
| `event=webhook_signature_header_missing` | `webhook-id` / `webhook-timestamp` / `webhook-signature` 헤더 중 하나라도 없음 | PortOne 대시보드에서 웹훅 URL 설정 및 헤더 포함 여부 확인 |
| `event=webhook_timestamp_invalid` | `webhook-timestamp` 값이 숫자가 아님 | PortOne 측 이슈 — 포트원 개발자 채널 문의 |
| `event=webhook_timestamp_expired` | 현재 시각과 timestamp 차이가 5분 초과 | 서버 시간이 NTP 동기화됐는지 확인 (`timedatectl`) |
| `event=webhook_signature_mismatch` | HMAC 계산 결과가 헤더 서명값과 다름 | `PORTONE_WEBHOOK_SECRET` 환경변수가 PortOne 대시보드의 시크릿과 일치하는지 확인 |
| `event=webhook_secret_decode_failed` | 시크릿이 유효한 Base64가 아님 | `PORTONE_WEBHOOK_SECRET` 값 재확인 (`whsec_` 접두사 포함 여부 포함) |

---

## 2. 웹훅을 받았는데 결제 상태가 안 바뀐다

### 2-1. `data` 또는 `data.paymentId`가 null

`event=webhook_missing_data` 로그 확인.

컨트롤러에서 200을 반환하고 서비스 호출 없이 종료합니다. PortOne이 보낸 웹훅 바디를 로그에서 확인해 `data.paymentId` 필드 존재 여부를 검토합니다.

### 2-2. orderId로 결제를 못 찾음 → 404

`PaymentNotFoundException` (PAYMENT_002).

`data.paymentId` 값이 우리 DB의 `orders.order_id`와 일치하는지 확인합니다. PortOne V2에서 `data.paymentId` = 우리가 PortOne SDK에 넘긴 `paymentId` = 서버의 `orderId`입니다.

### 2-3. 이미 PENDING이 아닌 결제 → 중복 웹훅으로 종료

`event=webhook_skipped_duplicate` 로그 확인.

정상 동작입니다. 동일 이벤트가 두 번 이상 왔고, 첫 번째에서 이미 처리됐습니다.

### 2-4. PortOne API 상태 또는 금액 불일치

| 로그 이벤트 | 의미 |
|---|---|
| `event=webhook_status_mismatch` | PortOne 조회 결과 상태가 `PAID`가 아님 |
| `event=webhook_amount_mismatch` | PortOne 조회 결과 금액이 DB 저장 금액과 다름 |

두 경우 모두 결제를 확정하지 않고 종료합니다. PortOne 대시보드에서 해당 결제 건의 실제 상태와 금액을 확인합니다.

---

## 3. PortOne API 호출이 실패한다 → 500

`PortOneApiException` (PAYMENT_003) — `event=portone_api_error` 로그 확인.

| 확인 항목 | 방법 |
|---|---|
| `PORTONE_API_SECRET` 환경변수 | PortOne 대시보드 API 시크릿과 일치 여부 |
| PortOne API 서버 상태 | PortOne 공식 상태 페이지 확인 |
| 네트워크 아웃바운드 | 서버에서 `api.portone.io` 443 포트 접근 가능 여부 |

웹훅 처리 중 PortOne API 호출이 실패하면 500 응답 → PortOne이 웹훅을 재전송합니다.

---

## 4. PT 결제 중복 에러 (PAYMENT_001 / 409)

`PaymentDuplicateException`.

동일 유저(`userId`) + 동일 PT 코스(`ptCourseId`)로 이미 `PAID` 상태 결제가 존재합니다. 정상 동작입니다. 프론트엔드에서 구매 버튼 비활성화(`GET /api/payments/pt-courses/{ptCourseId}/my-status`)를 통해 사전 방어합니다.

---

## 5. 구독 결제 중복 에러 (PAYMENT_004 / 409)

`SubscriptionDuplicateException`.

아래 두 조건 중 하나에 해당합니다.

1. 현재 `ACTIVE` 구독이 존재 (`status = ACTIVE AND expired_at > now`)
2. `PENDING` 상태 구독 결제가 존재 (결제창 진행 중)

PENDING은 30분이 지나면 자동으로 `FAILED` 처리됩니다 (`expireStalePendingSubscriptions()`). 고객이 결제창을 이탈 후 30분 이내에 다시 시도하면 이 에러가 납니다.

---

## 6. Flyway 마이그레이션 실패로 서버가 안 뜬다

`V3.5__rename_portone_payment_id_to_transaction_id_in_payments.sql` 마이그레이션 실패 가능성.

```sql
ALTER TABLE payments RENAME COLUMN portone_payment_id TO transaction_id;
```

이미 컬럼이 `transaction_id`인 DB에 적용하려고 하면 실패합니다. Flyway `flyway_schema_history` 테이블에서 V3.5 체크섬과 실행 여부를 확인합니다.

---

## 7. 주요 로그 이벤트 키 목록

| 이벤트 키 | 발생 위치 | 의미 |
|---|---|---|
| `event=webhook_received` | `PaymentCommandService` | 웹훅 수신 시작 |
| `event=webhook_unknown_type` | `PaymentCommandService` | 지원하지 않는 이벤트 타입 |
| `event=webhook_skipped_duplicate` | `PaymentCommandService` | 중복 웹훅 (이미 처리됨) |
| `event=webhook_status_mismatch` | `PaymentCommandService` | PortOne 상태 불일치 |
| `event=webhook_amount_mismatch` | `PaymentCommandService` | PortOne 금액 불일치 |
| `event=webhook_paid_succeeded` | `PaymentCommandService` | 결제 확정 완료 |
| `event=webhook_subscription_created` | `PaymentCommandService` | 구독 생성 완료 |
| `event=webhook_failed` | `PaymentCommandService` | 결제 실패 처리 |
| `event=webhook_cancelled` | `PaymentCommandService` | 결제 취소 처리 |
| `event=webhook_signature_mismatch` | `PortOneWebhookVerifier` | 서명 불일치 |
| `event=portone_api_error` | `PortOnePaymentAdapter` | PortOne API 호출 실패 |
| `event=pt_payment_create_succeeded` | `PaymentCommandService` | PT 결제 생성 성공 |
| `event=subscription_payment_create_succeeded` | `PaymentCommandService` | 구독 결제 생성 성공 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
