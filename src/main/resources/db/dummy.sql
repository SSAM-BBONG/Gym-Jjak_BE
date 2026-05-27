use gymjjak_db;
-- 1. 유저 먼저
INSERT INTO users (username, password, name, nickname, phone, role, status)
VALUES ('test@test.com', '1234', '테스트', '테스트닉네임', '010-1234-5678', 'USER', 'ACTIVE');

-- 2. 파일
INSERT INTO files (uploader_id, original_name, stored_name, file_url, content_type, file_size, file_type, status)
VALUES (1, 'test.jpg', 'test.jpg', 'uploads/test.jpg', 'image/jpeg', 1024, 'BUSINESS_LICENSE', 'ACTIVE');