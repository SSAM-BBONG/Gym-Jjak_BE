# PortOne 결제 코드 리뷰

## 리뷰 범위

- `payments/payment` 결제 생성, 웹훅 처리, PortOne 결제 조회, 결제 상태 변경
- `payments/subscription` 구독 생성, 만료, 취소에 따른 권한 처리
- PortOne V2 웹훅 및 결제 조회 API 연동 흐름

## 현재 결제 흐름

1. 서버가 PT 또는 구독 결제 가격을 확정하고 `PENDING` 결제를 생성한다.
2. 프론트엔드가 서버에서 받은 결제 ID로 PortOne 결제를 요청한다.
3. PortOne이 웹훅으로 결제 상태 변경을 알린다.
4. 서버가 PortOne 결제 조회 API로 상태와 금액을 재검증한다.
5. 검증에 성공하면 결제를 `PAID`로 변경하고, 구독 상품이면 구독을 생성한다.

서버가 가격을 결정하고, 웹훅 이후 PortOne API로 금액을 재검증하는 큰 방향은 적절하다. 다만 웹훅은 중복, 지연, 순서 역전, 위조 요청을 전제로 처리해야 한다.

## 주요 개선 사항

### [치명적] 웹훅에서 `orderId`에 의존

`PaymentWebhookController`는 `request.data().orderId()`를 `ProcessWebhookCommand`에 전달한다.

PortOne V2 웹훅의 기본 결제 식별자는 `data.paymentId`이므로, 실제 이벤트에 `orderId`가 없으면 `findByOrderId(null)`로 이어져 결제 완료 처리가 실패할 수 있다.

**개선 방향**

- 서버가 생성해 PortOne에 전달한 `paymentId`를 로컬 `orderId`로 사용한다.
- 웹훅에서는 `data.paymentId`만 사용해 로컬 결제를 조회한다.
- 실제 PortOne V2 payload를 사용한 DTO 계약 테스트를 추가한다.

### [치명적] PortOne 결제 금액 필드 파싱 확인 필요

`PortOnePaymentAdapter`는 응답의 `amount.paid`를 읽는다. PortOne V2 결제 조회의 결제 금액은 `payment.amount.total` 기준으로 검증해야 한다.

현재 필드가 응답에 없으면 NPE가 발생하거나, 잘못된 금액 비교가 수행될 수 있다.

**개선 방향**

- `Map<String, Object>` 캐스팅 대신 PortOne SDK 또는 전용 응답 DTO를 사용한다.
- `paymentId`, 결제 상태, `amount.total`을 모두 명시적으로 검증한다.
- 내부 주문 금액과 PortOne 조회 금액이 다르면 결제를 확정하지 않는다.

### [치명적] 웹훅 서명 검증 누락

웹훅 URL은 외부 공개 엔드포인트인데, `webhookSecret` 설정값이 실제 검증에 사용되지 않는다. 특히 `Transaction.Failed`, `Transaction.Cancelled`는 PortOne API 재조회 없이 결제 상태를 변경한다.

이 상태에서는 외부 요청이 결제 실패 또는 취소 처리를 시도할 수 있다.

**개선 방향**

- 웹훅 원본 바디와 PortOne 서명 헤더를 사용해 서명을 검증한다.
- 모든 상태 이벤트는 PortOne 결제 조회 결과와 대조한다.
- 서명 검증 실패, 결제 ID 불일치, 금액 불일치는 감사 로그를 남기고 상태를 변경하지 않는다.

### [치명적] 중복 웹훅 동시 처리 시 구독 중복 생성 가능

`Transaction.Paid` 처리에서 일반 조회 후 `PENDING` 상태를 확인하고 구독을 생성한다. 동일한 웹훅이 동시에 도착하면 두 요청이 모두 `PENDING`을 읽고 구독을 각각 생성할 수 있다.

**개선 방향**

- `findByOrderIdForUpdate` 같은 비관적 락을 적용하거나 `@Version` 기반 낙관적 락을 사용한다.
- 또는 `WHERE status = 'PENDING'` 조건부 업데이트의 영향 행 수로 단일 처리 여부를 보장한다.
- 구독 생성과 결제 상태 변경은 하나의 짧은 트랜잭션에서 원자적으로 처리한다.

### [치명적] 구독 만료와 취소 이후 권한 회수 누락

구독 조회와 활성 여부 검증이 `ACTIVE` 상태만 확인하며 `expiredAt`을 조건에 포함하지 않는다. `SubscriptionJpaEntity.expire()`를 호출하는 흐름도 없다.

결제 취소 웹훅은 결제만 `CANCELLED`로 전환하고, 이미 생성된 구독 권한은 종료하지 않는다.

**개선 방향**

- 구독 권한 조회 조건을 `ACTIVE AND expired_at > 현재 시각`으로 제한한다.
- 스케줄러 또는 조회 시점 만료 처리로 만료 상태를 갱신한다.
- 취소와 환불 정책에 따라 연결된 구독을 즉시 종료하거나 종료 예정 상태로 변경한다.

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

### [권장] 웹훅 입력 검증과 테스트 보강

웹훅 DTO에 `@Valid`, `@NotBlank`, 중첩 `@Valid`가 없어 `data` 또는 `paymentId`가 누락되면 예상하지 못한 500 오류가 발생할 수 있다.

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
