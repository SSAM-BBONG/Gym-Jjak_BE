// 유저 더미데이터 실행 : python k6/generate-users.py

import json
from pathlib import Path

PASSWORD = "Password123!"

DATA_DIR = Path("k6/data")
DATA_DIR.mkdir(parents=True, exist_ok=True)

without_onboarding = []
with_onboarding = []

# 회원가입은 되어 있지만 온보딩은 안 할 계정 30개
for i in range(1, 31):
    without_onboarding.append({
        "username": f"load_new_{i:03d}@test.com",
        "password": PASSWORD,
        "name": f"부하신규{i:03d}",
        "nickname": f"loadnew{i:03d}",
        "phone": f"01090{i:06d}"
    })

# 회원가입 + 온보딩까지 완료할 계정 50개
for i in range(1, 51):
    with_onboarding.append({
        "username": f"load_user_{i:03d}@test.com",
        "password": PASSWORD,
        "name": f"부하유저{i:03d}",
        "nickname": f"loaduser{i:03d}",
        "phone": f"01091{i:06d}"
    })

with open(DATA_DIR / "users-without-onboarding.json", "w", encoding="utf-8") as f:
    json.dump(without_onboarding, f, ensure_ascii=False, indent=2)

with open(DATA_DIR / "users-with-onboarding.json", "w", encoding="utf-8") as f:
    json.dump(with_onboarding, f, ensure_ascii=False, indent=2)

print("테스트 계정 JSON 생성 완료")
print(f"without onboarding: {len(without_onboarding)}명")
print(f"with onboarding: {len(with_onboarding)}명")