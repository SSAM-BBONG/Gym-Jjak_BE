# 매출 대시보드

## 목적

관리자 대시보드에서 짐짝 플랫폼의 최근 6개월 월별 매출을 운영 통계로 제공한다.
현재 범위는 통계 조회이며, 조직별 정산 신청, 청구, 지급 기능은 포함하지 않는다.

## 집계 기준

- 집계 원본은 `payments` 테이블의 `amount`다.
- `status = PAID`인 결제만 매출로 인정한다.
- 월 귀속 기준은 `paid_at`이며, 이번 달을 포함한 최근 6개 달력 월을 조회한다.
- 결제가 없는 달도 응답에서 제외하지 않고 금액 `0`으로 채운다.

## 매출 구성

| 구분 | 대상 | 월별 계산 방식 |
| --- | --- | --- |
| PT 수수료 매출 | `product_type = PT` | PT 결제 `amount` 합계 x 10% |
| 구독권 판매 매출 | `product_type = SUBSCRIPTIONS` | 구독권 결제 `amount` 합계 |

PT 수수료는 결제 건별로 계산하거나 반올림하지 않는다. 월별 PT 결제 금액을 먼저 합산한 뒤 10%를 적용하고, 원 단위 `HALF_UP`으로 한 번 반올림한다.

월별 최종 매출은 `PT 수수료 매출 + 구독권 판매 매출`이다.

## 데이터 저장 정책

현재는 `payments`에 `commission_amount` 또는 `net_revenue` 컬럼을 추가하지 않는다.

- 수수료율이 모든 조직과 기간에 고정된 10%다.
- 운영 통계 목적이므로 원본 결제 금액을 월별로 집계한 뒤 계산하는 비용이 충분히 작다.
- `net_revenue`는 PT에서는 수수료, 구독권에서는 결제금액이라는 서로 다른 의미를 담게 되어 컬럼 책임이 불명확하다.
- 구독권의 `net_revenue`는 `amount`와 중복 데이터가 된다.

## 구현 현황

- Endpoint: `GET /api/dashboard/admin/revenues`
- 권한: `ADMIN`
- 성공 코드: `ADMIN_DASHBOARD_200_4`
- 응답: `monthlyRevenues`에 최근 6개 월의 `month`, `ptCommissionRevenue`, `subscriptionRevenue`, `totalRevenue`를 반환한다.

조회 흐름은 아래와 같다.

```text
AdminDashboardController
-> AdminDashboardQueryUseCase
-> AdminDashboardQueryService
-> AdminRevenueQueryPort
-> AdminRevenueQueryAdapter
-> SpringDataPaymentRepository
-> payments 월별 집계 쿼리
```

- Repository는 `PAID` 결제만 대상으로 월별 PT·구독권 결제금액을 Projection으로 집계한다.
- Service는 조회 결과를 월별 Map으로 변환한 뒤 최근 6개월을 순회한다.
- DB 결과에 없는 월은 `0`으로 채우고, PT 수수료·총매출은 Service에서 계산한다.
- Payment Entity 전체를 로딩하지 않고 월별 합계만 조회한다.

## 테스트 범위

- 고정 `Clock` 기준으로 최근 6개 달력 월이 순서대로 반환되는지 검증한다.
- 결제가 없는 월이 모든 금액 `0`으로 채워지는지 검증한다.
- PT 월 매출의 10%가 원 단위 `HALF_UP`으로 반올림되는지 검증한다.
- Payment Repository Projection이 Dashboard Port의 원본 데이터로 변환되는지 검증한다.

## 운영 확인 항목

- `payments.paid_at`은 `DATETIME`이므로 결제 저장 시점과 대시보드 조회 시점의 시간대 기준이 같아야 한다. 현재 운영 JVM과 DB가 `Asia/Seoul` 기준인지 배포 전 확인한다.
- 결제 데이터가 충분히 누적되면 `status`, `paid_at` 조건의 실행 계획을 `EXPLAIN`으로 확인하고, 필요 시 복합 인덱스를 추가한다.

## 향후 확장 시점

실제 정산, 조직별 수수료율, 기간별 수수료 정책, 부분 환불 정산이 도입되면 별도 정산 모델을 설계한다.

- `settlement`: 조직 단위 정산 요청 및 승인 상태
- `settlement_item`: 결제별 정산 대상 금액과 확정 수수료
- 수수료율과 조직 귀속 정보는 정산 또는 결제 완료 시점 기준으로 스냅샷 보관

이 시점에는 단순 대시보드 집계값과 실제 지급/청구 기준을 분리한다.
