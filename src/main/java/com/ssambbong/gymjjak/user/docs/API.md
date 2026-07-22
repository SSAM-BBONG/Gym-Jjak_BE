# 👤 사용자 API

> 작성일: 2026-07-21  
> 대상: `UserAuthController`, `TokenController`, `UserController`  
> 공통 응답 형식: `status`, `code`, `message`, `data`

## 기능 개요

- 비회원은 일반 회원가입·로그인·임시 비밀번호 발급과 토큰 재발급을 요청할 수 있습니다.
- 인증 회원은 로그아웃, 프로필 조회·수정, 비밀번호 확인·변경, 회원 탈퇴를 수행합니다.
- 관리자는 전체 회원·트레이너·블랙리스트 목록을 조회하고 회원 상태를 변경합니다.
- 로그인 성공 시 Access Token은 응답 본문으로, Refresh Token은 HttpOnly 쿠키로 전달됩니다.

---

## 1. 일반 회원가입

`POST /api/auth/signup`

### Request Body

```json
{
  "username": "user@example.com",
  "password": "Test1234!",
  "name": "홍길동",
  "nickname": "길동이",
  "phone": "010-1111-2222"
}
```

| 필드 | 필수 | 설명 |
| --- | --- | --- |
| `username` | O | 이메일 형식의 로그인 아이디입니다. |
| `password` | O | 8~16자이며 영문·숫자·특수문자를 각각 하나 이상 포함해야 합니다. |
| `name` | O | 사용자 이름입니다. |
| `nickname` | O | 서비스에서 사용할 닉네임입니다. |
| `phone` | O | 전화번호입니다. |

### 성공 응답

`201 Created`, code `USER_REGISTERED`, `data: null`

### 주요 실패 코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `400` | `COMMON_400`, `USER_400_001` | 요청값 또는 비밀번호 정책이 올바르지 않습니다. |
| `409` | `USER_409_001` | 이메일이 중복됩니다. |
| `409` | `USER_409_002` | 닉네임이 중복됩니다. |
| `409` | `USER_409_003` | 전화번호가 중복됩니다. |

---

## 2. 로그인

`POST /api/auth/login`

### Request Body

```json
{
  "username": "user@example.com",
  "password": "Test1234!"
}
```

### 성공 응답

```json
{
  "status": 200,
  "code": "USER_LOGGEDIN",
  "message": "로그인이 완료되었습니다.",
  "data": {
    "accessToken": "eyJ...",
    "role": "USER",
    "onboardingCompleted": false
  }
}
```

응답에는 `refreshToken` 이름의 `HttpOnly`, `Secure`, `SameSite=Lax`, `Path=/`, 14일 유효 쿠키가 함께 설정됩니다.

| 실패 코드 | 설명 |
| --- | --- |
| `USER_401_001` | 아이디 또는 비밀번호가 일치하지 않습니다. |
| `USER_403_001` | 정지 등으로 로그인이 제한된 계정입니다. |
| `USER_400_SOCIAL_LOGIN_REQUIRED` | 소셜 로그인으로 생성된 계정입니다. |

---

## 3. 로그아웃

`POST /api/auth/logout`

- 인증 필요: `Authorization: Bearer {accessToken}`
- 서버에 저장된 Refresh Token을 삭제합니다.
- `accessToken`, `refreshToken` 쿠키를 만료시킵니다.
- 성공: `200 OK`, code `USER_LOGGEDOUT`

---

## 4. 임시 비밀번호 발급

`POST /api/auth/password`

```json
{
  "username": "user@example.com"
}
```

- 이메일 형식의 `username`이 필수입니다.
- 등록된 일반 회원의 비밀번호를 임시 비밀번호로 변경하고 메일로 전달합니다.
- 성공: `200 OK`, code `TEMPORARY_PASSWORD_SENT`

---

## 5. 소셜 회원 추가 정보 입력

`PATCH /api/auth/social/complete`

```json
{
  "nickname": "길동이",
  "phone": "010-1111-2222"
}
```

- 인증된 소셜 회원만 호출할 수 있습니다.
- 성공: `200 OK`, code `SOCIAL_SIGNUP_COMPLETED`
- 일반 회원이면 `USER_400_NOT_SOCIAL_USER`, 이미 입력했다면 `USER_409_SOCIAL_SIGNUP_ALREADY_COMPLETED`가 발생합니다.

---

## 6. Access Token 검증·재발급

### Access Token 검증

`GET /api/token/validate`

- Bearer Access Token이 필요합니다.
- 성공: `200 OK`, code `USER_200_004`
- 토큰이 만료되거나 유효하지 않으면 인증 필터가 `401`을 반환합니다.

### Access Token 재발급

`POST /api/token/reissue`

- Request Body가 아니라 `refreshToken` 쿠키를 사용합니다.

```json
{
  "status": 200,
  "code": "TOKEN_REISSUED",
  "message": "Access 토큰이 재발급되었습니다.",
  "data": { "accessToken": "eyJ..." }
}
```

| 실패 코드 | 설명 |
| --- | --- |
| `USER_401_002` | Refresh Token이 유효하지 않습니다. |
| `USER_401_003` | 쿠키 또는 저장된 Refresh Token이 없습니다. |
| `USER_401_004` | 전달된 토큰과 서버 저장 토큰이 일치하지 않습니다. |
| `USER_404_001` | 토큰에 해당하는 사용자가 없습니다. |

---

## 7. 내 프로필 API

모든 API는 Bearer Access Token이 필요합니다.

| API | 요청 | 성공 code | 역할 |
| --- | --- | --- | --- |
| `POST /api/users/me/password-verification` | `{ "password": "Test1234!" }` | `USER_PASSWORD_VERIFIED` | 프로필 수정 전 현재 비밀번호 확인 |
| `GET /api/users/me` | 없음 | `USER_PROFILE_FOUND` | 이름·닉네임·전화번호·유료 이용 여부 조회 |
| `PATCH /api/users/me` | `{ "name", "nickname", "phone" }` | `USER_PROFILE_UPDATED` | 프로필 수정 |
| `PATCH /api/users/me/updatePassword` | `{ "newPassword", "checkNewPassword" }` | `PASSWORD_CHANGED` | 비밀번호 변경 |
| `DELETE /api/users/me` | 없음 | `USER_PROFILE_WITHDREW` | USER·TRAINER 회원 탈퇴 |
| `GET /api/users/mypage` | 없음 | `USER_PROFILE_FOUND` | 이메일·닉네임·소셜 회원 여부·커뮤니티 글 수 조회 |

프로필 조회 응답 예시:

```json
{
  "status": 200,
  "code": "USER_PROFILE_FOUND",
  "message": "회원 프로필 조회에 성공했습니다.",
  "data": {
    "name": "홍길동",
    "nickname": "길동이",
    "phone": "010-1111-2222",
    "paid": false
  }
}
```

비밀번호 변경 시 두 입력값이 같아야 하며, 기존 비밀번호와 같은 값은 사용할 수 없습니다. 닉네임과 전화번호를 변경하면 중복 검증을 다시 수행합니다.

---

## 8. 관리자 회원 API

모든 API는 `ADMIN` 권한이 필요합니다.

### 목록 조회

| API | 대상 | 결과 필드 |
| --- | --- | --- |
| `GET /api/users/all?keyword=&page=0&size=20` | 전체 회원 | `userId`, `username`, `name`, `nickname`, `status` |
| `GET /api/users/trainer?keyword=&page=0&size=20` | 트레이너 | `trainerProfileId`, `userId`, `username`, `name`, `nickname`, `status` |
| `GET /api/users/blacklist?keyword=&page=0&size=20` | 블랙리스트 회원 | 회원 정보, `blacklistType`, `reason` |

세 API 모두 `USER_FOUND` 코드와 아래 페이지 구조를 반환합니다.

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "hasNext": false,
  "hasPrevious": false
}
```

### 회원 상태 변경

`PATCH /api/users/{userId}/status`

```json
{
  "status": "DAY_7",
  "reason": "부적절한 게시글 반복 작성"
}
```

| 상태 | 의미 | 사유 |
| --- | --- | --- |
| `ACTIVE` | 정상 상태로 복구 | 선택 |
| `DAY_7` | 7일 정지 | 필수 |
| `ETERNAL` | 영구 정지 | 필수 |

`WITHDRAWN`은 관리자 상태 변경 API에서 선택할 수 없습니다. 성공 시 code `USER_STATUS_UPDATED`를 반환합니다.

---

## 문서 정보

- 업데이트일: `2026-07-21`
- 현재 Controller, Request/Response DTO, 응답 코드와 도메인 오류 코드를 기준으로 작성했습니다.

