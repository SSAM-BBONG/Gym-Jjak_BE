CREATE TABLE meal_analysis
(
    meal_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    meal_type   VARCHAR(20)   NOT NULL,
    meal_time   DATETIME(6)   NOT NULL,
    menu        VARCHAR(255)  NOT NULL,
    file_id     BIGINT        NULL,
    kcal        BIGINT        NULL,
    created_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                      ON UPDATE CURRENT_TIMESTAMP(6),

    INDEX idx_meal_analysis_user_id (user_id),
    INDEX idx_meal_analysis_user_meal_time (user_id, meal_time),
    INDEX idx_meal_analysis_file_id (file_id)
);


CREATE TABLE nutrition_goals
(
    goal_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    goal_pro         BIGINT      NOT NULL,
    goal_car         BIGINT      NOT NULL,
    goal_fat         BIGINT      NOT NULL,
    daily_goal_kcal  BIGINT      NOT NULL,
    created_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                          ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT uk_nutrition_goals_user_id UNIQUE (user_id)
);