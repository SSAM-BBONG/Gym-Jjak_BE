# 🍽️ 식단 API

> 대상: `NutritionGoalController`, `MealAnalysisController`, `AiMealAnalysisController` · 모든 API 인증 필요

## 영양 목표

공통 경로: `/api/diet/nutrition-goals`

| API | 역할 |
| --- | --- |
| `POST` | 사용자별 영양 목표 1건 등록 |
| `GET` | 내 영양 목표 조회 |
| `PATCH` | 전달한 목표 필드만 수정 |
| `DELETE` | 내 영양 목표 삭제 |

```json
{
  "goalProtein": 120,
  "goalCarbohydrate": 250,
  "goalFat": 60,
  "dailyGoalKcal": 2000
}
```

모든 값은 등록 시 필수이며 0 이상입니다. 사용자는 목표를 한 건만 가질 수 있습니다. 수정 요청은 하나 이상의 필드를 포함해야 하며 값은 `null`일 수 없습니다.

## 식단 관리

공통 경로: `/api/diet/meals`

| API | 역할 |
| --- | --- |
| `POST` | 수동 식단 등록 |
| `GET /{mealId}` | 내 식단 단건 조회 |
| `GET?page=0&size=20` | 내 식단 최신순 페이지 조회 |
| `PATCH /{mealId}` | 내 식단 부분 수정 |
| `DELETE /{mealId}` | 내 식단 삭제 |
| `POST /ai-analyze` | 이미지 AI 분석 후 식단 저장 |

수동 등록 요청:

```json
{
  "mealType": "아침",
  "mealTime": "2026-07-21 08:30",
  "menu": "삶은 계란 2개와 바나나",
  "kcal": 280,
  "carbohydrate": 67.00,
  "protein": 51.90,
  "fat": 7.60,
  "file": {
    "fileKey": "uploads/meals/1/550e8400-e29b-41d4-a716-446655440000",
    "originalName": "meal.jpg",
    "contentType": "image/jpeg",
    "fileSize": 524288
  }
}
```

- 식사 유형은 `아침`, `점심`, `저녁`, `간식`입니다.
- 식사 일시는 `yyyy-MM-dd HH:mm`, 메뉴는 필수이며 최대 255자입니다.
- 영양값은 0 이상입니다. 탄수화물·단백질·지방 입력 또는 변경은 활성 AI 구독자만 가능합니다.
- 조회·수정·삭제는 `userId + mealId` 조건을 사용해 타인의 식단 존재 여부를 노출하지 않습니다.
- 목록 `page`는 최소 0으로, `size`는 1~100으로 보정됩니다.

PATCH 이미지 변경 규칙:

- `file` 필드를 생략하면 기존 이미지를 유지합니다.
- `"file": null`을 전달하면 기존 이미지를 식단에서 제거합니다.
- `file` 메타데이터를 전달하면 새 파일을 `MEAL_IMAGE`로 등록하고 생성된 `fileId`로 교체합니다.

## AI 분석 요청

```json
{
  "file": {
    "fileKey": "meal/uuid.jpg",
    "originalName": "lunch.jpg",
    "contentType": "image/jpeg",
    "fileSize": 102400
  },
  "mealType": "점심",
  "mealTime": "2026-07-21 12:30"
}
```

프론트가 업로드를 완료한 이미지 메타데이터를 전달합니다. 성공 응답에는 식단·영양값 외에 `evaluation`, `confidence`, `warnings`가 포함됩니다. 활성 AI 구독이 없으면 AI 분석과 영양성분 저장을 사용할 수 없습니다.

## 주요 응답 데이터

- 영양 목표: `goalId`, 목표 단백질·탄수화물·지방·칼로리, 생성·수정 시각
- 식단: `mealId`, 식사 유형·시각, 메뉴, 영양값, `fileId`, 생성·수정 시각
- 목록: `meals`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`

## 문서 정보

- 업데이트일: `2026-07-21`
