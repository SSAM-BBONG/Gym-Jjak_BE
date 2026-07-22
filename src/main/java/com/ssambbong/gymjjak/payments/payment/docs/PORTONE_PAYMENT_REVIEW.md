# PortOne 결제 코드 리뷰

## 리뷰 범위

- `payments/payment` 결제 생성, 웹훅 처리, PortOne 결제 조회, 결제 상태 변경
- `payments/subscription` 구독 생성, 만료, 취소에 따른 권한 처리
- PortOne V2 웹훅 및 결제 조회 API 연동 흐름

## 현재 결제 흐름

1. 서버가 PT 또는 구독 결제 가격을 확정하고 `PENDING` 결제를 생성한다.
2. 프론트엔드가 서버에서 받은 결제 ID로 PortOne 결제를 요청한다.
3. PortOne이 웹훅으로 결제 상태 변경을 알린다.
4. 서버가 웹훅 시그니처를 검증한 뒤 PortOne 결제 조회 API로 상태와 금액을 재검증한다.
5. 검증에 성공하면 결제를 `PAID`로 변경하고, 구독 상품이면 구독을 생성한다.

서버가 가격을 결정하고, 웹훅 이후 PortOne API로 금액을 재검증하는 큰 방향은 적절하다. 다만 웹훅은 중복, 지연, 순서 역전, 위조 요청을 전제로 처리해야 한다.

## 주요 개선 사항

### ~~[치명적] 웹훅에서 `orderId`에 의존~~ ✅ 수정 완료

PortOne V2 웹훅에서 `data.orderId`는 존재하지 않는다. `data.paymentId` = 서버가 생성해 PortOne에 전달한 주문 ID(우리 `orderId`)이고, `data.transactionId` = PortOne 내부 거래 고유 ID이다.

- `WebhookPaymentRequest.WebhookData`에서 `orderId` 제거, `transactionId` 추가
- 컨트롤러가 `data.paymentId` → `orderId`로, `data.transactionId` → `transactionId`로 전달
- DB 컬럼 `portone_payment_id` → `transaction_id`로 rename (V3.5 마이그레이션)
- `Payment` 도메인 필드 `portonePaymentId` → `transactionId`

### ~~[치명적] PortOne 결제 금액 필드 파싱 확인 필요~~ ✅ 수정 완료

`PortOnePaymentAdapter`가 `amount.paid`를 읽던 코드를 `amount.total`로 수정했다. PortOne V2 결제 조회 응답에서 실제 결제 금액은 `amount.total` 기준이다.

추가로 `Map<String, Object>` 방식을 `PortOnePaymentResponse` 전용 DTO로 교체하여 타입 안전성을 확보했다. (`infrastructure/portone/PortOnePaymentResponse.java`)

### ~~[치명적] 웹훅 서명 검증 누락~~ ✅ 수정 완료

`infrastructure/portone/PortOneWebhookVerifier` 컴포넌트를 추가하여 Standard Webhooks 스펙 기반 서명 검증을 구현했다.

- `PaymentWebhookController`가 `@RequestBody String rawBody`로 원본 바디를 수신
- `webhook-id`, `webhook-timestamp`, `webhook-signature` 헤더 존재 확인
- timestamp가 현재 시각 ±5분 이내인지 검증 (재전송 공격 방어)
- `HMAC-SHA256(Base64Decode(secret), "{id}.{timestamp}.{rawBody}")` 재계산 후 헤더 서명값과 비교
- 검증 실패 시 `WebhookInvalidSignatureException` → 400 반환
- `data` 또는 `data.paymentId` null 시 200 반환 후 종료 (NPE 방지)

### ~~[치명적] 중복 웹훅 동시 처리 시 구독 중복 생성 가능~~ ✅ 코드 확인 결과 이미 처리됨

`PaymentCommandService.processWebhook()`에서 구독 결제의 경우 트랜잭션 내에서 `subscriptionUserPort.lockById(userId)`로 사용자 행을 잠근 뒤 `findByOrderId()`로 결제를 재조회한다. 첫 번째 웹훅이 커밋되면 두 번째 웹훅은 재조회 시 `PENDING`이 아님을 확인하고 종료한다.

### ~~[치명적] 구독 만료와 취소 이후 권한 회수 누락~~ ✅ 코드 확인 결과 이미 처리됨

- **취소**: `Transaction.Cancelled` 처리 시 `subscriptionLifecyclePort.expire(subscriptionId)`로 구독을 EXPIRED 처리하고, 활성 구독이 없으면 `subscriptionUserPort.markAsUnpaid(userId)`로 권한을 회수한다.
- **만료**: `SubscriptionExpirationScheduler` + `SubscriptionExpirationBatchService`가 주기적으로 `expiredAt <= now`인 ACTIVE 구독을 EXPIRED로 전환하고 사용자 권한을 회수한다.
- **조회**: `findByUserIdAndStatusAndExpiredAtAfter(userId, ACTIVE, now)`로 만료 시각까지 조건에 포함해 활성 구독을 조회한다.

### [중요] 웹훅 재시도와 부분 취소 정책 필요

현재는 `Transaction.Paid`, `Transaction.Failed`, `Transaction.Cancelled`만 처리한다. 부분 취소, PortOne 조회 실패, DB 일시 장애에 대한 정책이 명시되어 있지 않다.

**권장 정책**

- 서명 검증 실패, 금액 불일치, 존재하지 않는 내부 결제는 감사 로그 후 상태를 변경하지 않는다.
- PortOne API 또는 DB의 일시 장애는 실패 응답으로 재시도 가능하게 한다.
- 부분 취소를 아직 지원하지 않으면 명시적으로 경고 로그를 남기고, 환불 도메인 확장 시 별도 상태와 금액 정책을 설계한다.

### [권장] 시간 기준 통일

결제 및 구독 생성 코드가 `LocalDateTime.now()`를 직접 사용한다. 대시보드는 KST `Clock`을 사용하므로, 배포 JVM의 기본 시간대가 다르면 월별 집계 경계가 어긋날 수 있다.

**개선 방향**

- `Clock`을 주입한다.
- 결제 완료, 실패, 취소, 구독 시작 및 만료 시간을 `LocalDateTime.now(clock)`으로 생성한다.

### [권장] 테스트 보강

**추가할 테스트**

- 실제 PortOne V2 웹훅 payload 역직렬화 테스트
- 웹훅 서명 검증 성공 및 실패 테스트
- `amount.total` 금액 일치 및 불일치 테스트
- 동일 `Transaction.Paid` 웹훅 동시 수신 테스트
- 구독 만료 후 권한 차단 테스트
- 결제 취소 또는 환불 후 구독 권한 처리 테스트

## 권장 웹훅 확정 흐름

1. 서버가 결제 ID와 금액을 포함한 `PENDING` 결제를 생성한다.
2. 프론트엔드가 해당 결제 ID로 PortOne 결제를 요청한다.
3. 웹훅 수신 시 서명을 먼저 검증한다.
4. `paymentId`로 PortOne 결제를 조회한다.
5. 내부 결제 ID, 결제 상태, `amount.total`을 비교한다.
6. 락 또는 조건부 업데이트로 한 번만 결제를 확정한다.
7. 구독 상품이면 같은 트랜잭션에서 구독을 생성하고 결제에 연결한다.
8. 취소, 환불, 만료 이벤트는 구독 권한 상태까지 함께 갱신한다.

## 참고 자료

- [PortOne V2 웹훅 가이드](https://developers.portone.io/opi/ko/integration/webhook/readme-v2?v=v2)
- [PortOne V2 결제 완료 검증 가이드](https://developers.portone.io/opi/ko/integration/start/v2/checkout)
- [PortOne V2 결제 조회 API](https://developers.portone.io/api/rest-v2/payment?v=v2)
