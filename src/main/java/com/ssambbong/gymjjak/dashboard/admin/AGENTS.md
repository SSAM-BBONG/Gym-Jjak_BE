# 관리자 매출 대시보드 작업 기준

## 목적

관리자 대시보드는 최근 6개 **달력 월**의 짐짝 플랫폼 매출을 월별로 조회한다.
매출은 아래 두 카테고리의 합계로 구성한다.

1. PT 수수료 매출
2. AI 구독권 판매 매출

관리자 전용 기능이므로 Controller에서 `ADMIN` 권한을 검증한다.

## 공통 집계 규칙

- 집계 원본은 `payments` 테이블이다. `subscriptions` 테이블의 상태나 가격으로 매출을 계산하지 않는다.
- `payments.status = PAID`인 결제만 매출로 인정한다. `PENDING`, `FAILED`, `CANCELLED`는 제외한다.
- 월 귀속 기준은 주문 생성 시각이 아닌 `payments.paid_at`이다.
- 기간은 이번 달을 포함한 최근 6개 달력 월이다.
  - 시작: 기준일의 월에서 5개월 전, 해당 월 1일 00:00:00
  - 종료: 다음 달 1일 00:00:00 미만
  - 예: 2026-07 기준 `2026-02-01 00:00:00 <= paid_at < 2026-08-01 00:00:00`
- DB 결과에 결제 없는 월이 빠질 수 있으므로, 서비스에서 6개월 전체 월을 채워 응답한다. 없는 월의 금액은 0이다.
- 금액 단위는 원(KRW)이며, 응답 타입은 `long`을 사용한다.

## PT 수수료 매출

- 대상: `payments.product_type = PT`이고 `PAID` 상태인 결제
- 조직 귀속: `payments.pt_course_id -> pt_courses.organization_id` 조인으로 확인한다.
- 조직별 PT 총매출: 해당 월의 `payments.amount` 합계
- 짐짝 PT 수수료: **월별 PT 총매출의 10%**
  - 수수료는 결제 건별로 반올림하지 않고 월별 총매출 합계에 한 번 적용한다.
  - 원 단위 반올림은 `HALF_UP`, 소수점 0자리로 처리한다.
- 관리자 화면의 핵심 매출값은 PT 수수료이며, 필요하면 검증용으로 조직별 PT 총매출도 함께 제공한다.
- 과거 매출 조회에서는 `pt_courses.deleted_at IS NULL` 조건을 두지 않는다. 삭제된 PT 코스의 과거 완료 결제까지 제외하면 관리자 매출 이력이 변한다.

## 구독권 판매 매출

- 대상: `payments.product_type = SUBSCRIPTIONS`이고 `PAID` 상태인 결제
- 짐짝 구독 매출: 해당 월의 `payments.amount` 합계
- 구독권 결제는 짐짝 귀속 매출이므로 수수료를 별도로 적용하지 않는다.
- 구독 상태(`ACTIVE`, `EXPIRED`)는 결제 매출 집계 조건이 아니다. 결제 완료 상태만 판단한다.

## 월별 응답 기준

각 월에는 최소한 아래 값을 제공한다.

- `month`: `yyyy-MM`
- `ptCommissionRevenue`: 월별 PT 수수료 매출
- `subscriptionRevenue`: 월별 구독권 판매 매출
- `totalRevenue`: `ptCommissionRevenue + subscriptionRevenue`

조직별 상세 매출이 필요해질 경우에는 별도 조회 API로 분리한다. 월별 플랫폼 매출 API에 조직별 목록을 함께 넣지 않는다.

## 구현 원칙

- Dashboard Service가 Spring Data JPA Repository를 직접 호출하지 않는다.
- Dashboard 도메인 Repository/Port를 만들고, Payment persistence 영역에 조회 Adapter를 둔다.
- 집계 쿼리는 Projection 또는 DTO Projection으로 필요한 월·금액만 조회한다. Payment Entity 전체를 로딩한 뒤 Java에서 합산하지 않는다.
- 수수료 계산, 6개월 0값 채우기, 총매출 계산은 Dashboard Service의 책임이다.
- 조회 전용 서비스는 `@Transactional(readOnly = true)`를 사용한다.

## 향후 정책 확인 사항

- 부분 환불과 부분 취소가 도입되면 현재 `CANCELLED` 제외 정책만으로는 부족하다. 환불 금액 또는 정산 원장 모델을 별도로 설계한다.
- PT 코스의 조직 변경이 가능해지면 현재 조인 방식은 과거 결제를 새 조직 매출로 귀속시킬 수 있다. 정산 정확성이 필요해지는 시점에는 결제 완료 시점의 `organization_id`를 Payment에 스냅샷으로 저장한다.
- 수수료율이 조직별 또는 기간별로 달라질 가능성이 생기면 상수 `10%` 대신 수수료 정책/정산 이력을 도입한다.

## 현재 구현된 관리자 대시보드

Base URL은 `/api/dashboard/admin`이며, 두 API 모두 `ADMIN` 권한이 필요하다.

### 회원 현황

- Endpoint: `GET /api/dashboard/admin/members`
- 전체 활성 일반 사용자 수를 조회한다.
- `TrainerProfileStatus.ACTIVE`인 활성 트레이너 수를 조회한다.
- `OrganizationStatus.ACTIVE`인 활성 조직 수를 조회한다.
- 현재 월을 포함한 최근 6개월의 일반 사용자 가입자 수를 월별로 조회한다.
- 가입자 수가 없는 월도 `0`으로 채워 항상 6개월을 반환한다.
- 월 범위는 `Clock`을 기준으로 계산하며, 서버 시스템 시간에 직접 의존하지 않는다.
- 응답의 월별 항목은 `month(yyyy-MM)`, `count`로 구성한다.

### 콘텐츠 현황

- Endpoint: `GET /api/dashboard/admin/contents`
- `PtCourseStatus.VISIBLE`인 활성 PT 코스 수를 조회한다.
- `PtCourseStatus.BLOCKED`인 블라인드 PT 코스 수를 조회한다.
- 삭제되지 않은 신고 그룹 중 `ReportGroupReviewStatus.PENDING`인 처리 대기 신고 그룹 수를 조회한다.
- 응답 필드는 `activePtCourseCount`, `blindedPtCourseCount`, `pendingReportGroupCount`이다.

### 현재 구현 구조

- Controller는 `AdminDashboardQueryUseCase`를 호출하고, Result를 Response로 변환한다.
- 조회 서비스는 `@Transactional(readOnly = true)`로 동작한다.
- 회원·조직·트레이너·PT 코스·신고 그룹의 현재 통계는 각 도메인의 Spring Data Repository를 통해 조회하고 있다.
- 향후 매출 집계 기능부터는 이 폴더의 매출 Port를 기준으로 필요한 집계 데이터만 조회한다. 기존 구현을 확장하면서 서비스가 Payment Spring Data Repository에 직접 의존하지 않도록 경계를 정리한다.

### 테스트 기준

- 회원 현황은 활성 상태만 집계되는지 검증한다.
- 가입자 수가 없는 월이 `0`으로 반환되고, 항상 최근 6개월이 반환되는지 검증한다.
- 콘텐츠 현황은 VISIBLE, BLOCKED, PENDING 상태별 집계가 분리되는지 검증한다.
- 비관리자 요청은 `403 Forbidden`으로 차단되는지 검증한다.
- Clock을 고정한 테스트로 월 경계와 연도 변경 시점을 안정적으로 검증한다.
