# 🔔 구독 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 구독 **생성** 관련 이슈는 [payment/docs/TROUBLESHOOTING.md](../../payment/docs/TROUBLESHOOTING.md)를 함께 확인합니다.

---

## 1. 내 구독 조회 결과가 null이다

`GET /api/subscriptions/me` → `data: null`

조회 조건: `status = ACTIVE AND expired_at > now`

아래 순서로 확인합니다.

1. **구독 자체가 생성됐는지 확인** — DB에서 해당 userId의 subscriptions 레코드 존재 여부 조회
2. **구독이 EXPIRED 상태** — `expired_at <= now`이면 `SubscriptionExpirationScheduler`가 이미 만료 처리했거나, 만료됐지만 스케줄러가 아직 안 돌았어도 조회 시점 기준으로 만료된 것으로 판단해 null 반환 (정상 동작)
3. **구독이 CANCELLED 상태** — `Transaction.Cancelled` 웹훅 처리 시 `subscriptionLifecyclePort.expire()`로 EXPIRED 처리됨

---

## 2. 구독이 생성되지 않았다

구독은 `POST /api/payments/subscriptions` 결제 요청 후 PortOne 웹훅(`Transaction.Paid`) 처리 시 `SubscriptionCreatePort.create()`를 통해 생성됩니다.

### 확인 순서

1. **결제 상태 확인** — `payments` 테이블에서 해당 `orderId`의 `status`가 `PAID`인지 확인
   - `PENDING`: 웹훅이 아직 안 왔거나 처리 중
   - `FAILED`: 결제 자체가 실패
2. **웹훅 처리 로그 확인**
   - `event=webhook_subscription_created` 로그가 있으면 구독이 생성됐고 DB에서 찾아야 함
   - `event=webhook_status_mismatch` 또는 `event=webhook_amount_mismatch` 로그가 있으면 검증 불일치로 생성 안 됨
3. **서명 검증 실패 여부** — `event=webhook_signature_mismatch` 로그 확인 (웹훅 자체가 처리 안 됨)

---

## 3. 구독 만료 처리가 안 된다

`SubscriptionExpirationScheduler`가 주기적으로 `SubscriptionExpirationBatchService.expireNextBatch(now, batchSize)`를 호출합니다.

### 확인 항목

| 확인 항목 | 방법 |
|---|---|
| 스케줄러 동작 여부 | `event=subscription_expiration_batch_completed` 로그 확인 |
| 만료 대상 존재 여부 | `status = ACTIVE AND expired_at <= now` 조건으로 DB 직접 조회 |
| 배치 처리 결과 | 로그에서 `expiredCount`, `unpaidUserCount` 값 확인 |

스케줄러는 배포 환경에서만 동작합니다. 로컬 개발 환경에서 만료 테스트가 필요하면 `SubscriptionExpirationBatchService.expireNextBatch()`를 직접 호출합니다.

---

## 4. 결제 취소 후 구독 권한이 회수되지 않았다

`Transaction.Cancelled` 웹훅 처리 흐름:

```
결제 취소 웹훅
  → subscriptionLifecyclePort.expire(subscriptionId)  ← 구독 EXPIRED 처리
  → paymentRepository.update(payment.cancel(cancelledAt))
  → subscriptionPaymentQueryPort.existsActiveByUserId(userId, cancelledAt) 확인
      → 다른 활성 구독 없으면 subscriptionUserPort.markAsUnpaid(userId)
```

### 확인 항목

1. `event=webhook_cancelled` 로그가 있는지 확인 (웹훅이 처리됐는지)
2. `payment.getAiSubscriptionId()`가 null이 아닌지 확인 — null이면 구독 만료 처리 없이 결제만 취소됨
3. 같은 유저의 다른 활성 구독이 있으면 `markAsUnpaid()`를 호출하지 않음 (정상 동작)

---

## 5. 구독 플랜 가격이 다르게 보인다

`GET /api/subscriptions/plans` 응답의 가격은 DB를 조회하지 않고 `SubscriptionPlanType` enum에서 읽습니다.

가격을 변경하려면 enum을 수정하고 재배포해야 합니다. DB에서 가격을 바꿔도 반영되지 않습니다.

---

## 6. 주요 로그 이벤트 키 목록

| 이벤트 키 | 발생 위치 | 의미 |
|---|---|---|
| `event=webhook_subscription_created` | `PaymentCommandService` | 구독 생성 완료 |
| `event=subscription_expiration_batch_completed` | `SubscriptionExpirationScheduler` | 만료 배치 처리 완료 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
