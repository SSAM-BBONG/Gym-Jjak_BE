# 💬 커뮤니티 API

> 대상: `CommunityController` · 공통 경로: `/api/community`

## API 목록

| API | 역할 | 성공 code |
| --- | --- | --- |
| `POST /posts` | 게시글 작성 | `COMMUNITY_201_001` |
| `GET /posts` | 전체 게시글 페이지 조회 | `COMMUNITY_200_001` |
| `GET /posts/me` | 내 게시글 페이지 조회 | `COMMUNITY_200_001` |
| `GET /posts/{postId}` | 상세·댓글 커서 조회 | `COMMUNITY_200_002` |
| `PATCH /posts/{postId}` | 내 게시글 수정 | `COMMUNITY_200_003` |
| `DELETE /posts/{postId}` | 내 게시글 삭제 | `COMMUNITY_200_004` |
| `POST /posts/{postId}/comments` | 댓글 작성 | `COMMUNITY_201_002` |
| `PATCH /comments/{commentId}` | 내 댓글 수정 | `COMMUNITY_200_005` |
| `DELETE /comments/{commentId}` | 내 댓글 삭제 | `COMMUNITY_200_006` |
| `POST /posts/{postId}/likes` | 좋아요 | `COMMUNITY_201_003` |
| `DELETE /posts/{postId}/likes` | 좋아요 취소 | `COMMUNITY_200_007` |

## 게시글

```json
{
  "type": "FREE",
  "title": "운동 루틴 공유합니다",
  "content": "오늘 진행한 루틴입니다."
}
```

- 유형은 `FREE`, `NOTICE`입니다.
- 제목은 필수이며 최대 100자, 내용도 필수입니다.
- `NOTICE`는 `ADMIN`만 작성할 수 있고 위반 시 `COMMUNITY_403_001`입니다.
- 쓰기 API는 `USER`, `TRAINER`, `ADMIN`, `ORGANIZATION` 권한이 필요합니다.
- 수정·삭제는 작성자만 가능합니다.

목록은 `type`, 제목 `keyword`, `page`(기본 0), `size`(기본 20)를 받습니다. 상세는 `commentCursorId`와 `commentSize`로 댓글을 커서 방식으로 조회하며 현재 사용자의 좋아요 여부 등을 함께 반환합니다.

## 댓글과 좋아요

댓글 등록·수정 요청:

```json
{ "content": "좋은 정보 감사합니다." }
```

- 댓글 수정·삭제는 작성자만 가능합니다.
- 같은 사용자가 같은 게시글을 다시 좋아요하면 `COMMUNITY_409_001`입니다.
- 존재하지 않는 좋아요를 취소하면 `COMMUNITY_404_003`입니다.

## 주요 실패 코드

| code | 의미 |
| --- | --- |
| `COMMUNITY_404_001` | 게시글 없음 |
| `COMMUNITY_403_002`, `COMMUNITY_403_003` | 게시글 수정·삭제 소유권 없음 |
| `COMMUNITY_403_004`, `COMMUNITY_403_005` | 댓글 수정·삭제 소유권 없음 |
| `COMMUNITY_409_001` | 좋아요 중복 |

## 문서 정보

- 업데이트일: `2026-07-21`

